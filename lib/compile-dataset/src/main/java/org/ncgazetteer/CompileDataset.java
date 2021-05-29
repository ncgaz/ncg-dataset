package org.ncgazetteer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class CompileDataset {

    public static void main(String[] args) {
        if (args.length == 3) {

            Path updatesDir = Paths.get(args[0]);
            Path diffsDir = Paths.get(args[1]);
            Path versionsDir = Paths.get(args[2]);

            if (Files.isDirectory(updatesDir)) {
                Model latestVersion = processUpdates(updatesDir, diffsDir, versionsDir);
                if (latestVersion == null) {
                    System.exit(1);
                } else {
                    RDFDataMgr.write(System.out, latestVersion, RDFFormat.TURTLE_PRETTY);
                    System.exit(0);
                }
            } else {
                err("%s is not a directory", updatesDir);
            }
        }

        usage();
        System.exit(1);
    }

    private static DirectoryStream.Filter<Path> UPDATE_FILE_FILTER =
            new DirectoryStream.Filter<Path>() {
                public boolean accept(Path path) throws IOException {
                    String filename = path.getFileName().toString();
                    return UPDATE_FILE_PATTERN.matcher(filename).matches();
                }
            };

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static enum Operation {
        ADD, REPLACE
    }

    private static Pattern UPDATE_FILE_PATTERN = Pattern.compile(String.format(
            "^(?<date>\\d{4}-\\d{2}-\\d{2})-(?<tag>[a-z-]+)-(?<op>%s)\\.ttl$",
            Stream.of(Operation.values())
                    .map(String::valueOf)
                    .collect(Collectors.joining("|"))));

    private static Model processUpdates(Path updatesDir, Path diffsDir, Path versionsDir) {
        try (DirectoryStream<Path> stream =
                Files.newDirectoryStream(updatesDir, UPDATE_FILE_FILTER)) {

            Iterator<Path> iter = StreamSupport.stream(stream.spliterator(), false)
                    .sorted(Comparator.comparing(Path::toString)).iterator();

            Model previousVersion = ModelFactory.createDefaultModel();

            while (iter.hasNext()) {
                previousVersion = processUpdate(
                        iter.next(), previousVersion, diffsDir, versionsDir
                );
                if (previousVersion == null) {
                    break;
                }
            }

            return previousVersion;

        } catch (IOException e) {
            err("Error reading %s: %s", updatesDir, e);
            return null;
        }
    }



    private static Model processUpdate(
            Path updateFile,
            Model previousVersion,
            Path diffsDir,
            Path versionsDir
    ) {
        Matcher m = UPDATE_FILE_PATTERN.matcher(updateFile.getFileName().toString());

        if (m.matches()) {
            try {
                Date date = DATE_FORMAT.parse(m.group("date"));
                String tag = m.group("tag");
                Operation operation = Operation.valueOf(m.group("op"));

                // copy previous version to current version
                Model currentVersion = ModelFactory.createDefaultModel();
                currentVersion.add(previousVersion);

                Model update = RDFDataMgr.loadModel(updateFile.toString());
                Model removed = null;

                try {

                    switch (operation) {
                        case ADD:
                            // add will happen below
                            break;

                        case REPLACE:
                            removed = removeReplacedStatements(
                                    currentVersion, update, diffsDir, date, tag);
                            if (removed == null) {
                                err("Failed to remove statements replaced by %s", updateFile);
                                return null;
                            }
                            break;

                        default:
                            err("Unknown operation: %s", updateFile);
                            return null;
                    }

                    boolean ok = false;

                    currentVersion.add(update);

                    ok = write(currentVersion, versionsDir, "%s-%s.ttl", date, tag);
                    if (!ok) {
                        return null;
                    }

                    // if we're adding statements, don't include
                    // statements that were already in the dataset in
                    // the diff. if we're removing statements, don't
                    // include statements that are replaced in the
                    // diff.
                    List<Statement> redundant;
                    if (removed == null) {
                        redundant = update.intersection(previousVersion)
                                .listStatements().toList();
                    } else {
                        redundant = update.intersection(removed)
                                .listStatements().toList();

                        removed.remove(redundant);
                        ok = write(removed, diffsDir, "%s-%s/removed.ttl", date, tag);
                        if (!ok) {
                            return null;
                        }
                    }

                    update.remove(redundant);
                    ok = write(update, diffsDir, "%s-%s/added.ttl", date, tag);
                    if (!ok) {
                        return null;
                    }

                    return currentVersion;

                } finally {
                    previousVersion.close();
                    update.close();
                    if (removed != null) {
                        removed.close();
                    }
                }
            } catch (ParseException e) {
                err("Invalid date %s: %s", updateFile, e);
                return null;
            }
        } else {
            err("Malformed filename: %s", updateFile);
            return null;
        }
    }

    private static Model removeReplacedStatements(
            Model currentVersion,
            Model update,
            Path diffsDir,
            Date date,
            String tag
    ) {
        Model removed = ModelFactory.createDefaultModel();
        removed.setNsPrefixes(update);

        StmtIterator updateStmts = update.listStatements();
        while (updateStmts.hasNext()) {
            Statement updateStmt = updateStmts.next();
            Resource s = updateStmt.getSubject();
            Property p = updateStmt.getPredicate();

            if (s.isURIResource()) {
                List<Statement> toRemove =
                        currentVersion.listStatements(s, p, (RDFNode) null).toList();

                currentVersion.remove(toRemove);
                removed.add(toRemove);
            } else {
                err("Blank subject node found in update");
                removed.close();
                return null;
            }
        }
        return removed;
    }

    private static boolean write(Model m, Path dir, String rest, Date date, String tag) {
        if (m.isEmpty()) {
            return true;
        }

        Path p = dir.resolve(String.format(rest, DATE_FORMAT.format(date), tag));
        Path d = p.getParent();
        try {
            if (!Files.exists(d)) {
                Files.createDirectories(d);
            }
            try (OutputStream out = Files.newOutputStream(p, StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {

                RDFDataMgr.write(out, m, RDFFormat.TURTLE_PRETTY);
            }
            return true;
        } catch (IOException e) {
            return err("Failed to write triples to %s", p);
        }
    }

    private static boolean err(String msg) {
        System.err.println(msg);
        return false;
    }

    private static boolean err(String msgTemplate, Path p) {
        System.err.println(String.format(msgTemplate, p));
        return false;
    }

    private static boolean err(String msgTemplate, Path p, Exception e) {
        System.err.println(String.format(msgTemplate, p, e));
        return false;
    }

    private static void usage() {
        System.err.println();
        System.err.println("usage:");
        System.err.println("  compile-dataset updates versions diffs");
        System.err.println();
        System.err.println("  `updates` should be a directory of Turtle files named like: ");
        System.err.println("    YYYY-MM-DD-tag-[ADD|REPLACE].ttl");
        System.err.println();
        System.err.println("  `diffs` is the directory where diffs between versions will");
        System.err.println("    be written. It will be created if it does not exist");
        System.err.println();
        System.err.println("  `versions` is the directory where versions of the dataset will");
        System.err.println("    be written. It will be created if it does not exist");
        System.err.println();
    }
}
