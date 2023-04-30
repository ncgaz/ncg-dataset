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
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.update.UpdateAction;

public class CompileDataset {

    public static void main(String[] args) {
        if (args.length == 3) {

            Path updatesDir = Paths.get(args[0]);
            Path diffsDir = Paths.get(args[1]);
            Path versionsDir = Paths.get(args[2]);

            if (checkDir(updatesDir)) {
                Model latestVersion = processUpdates(updatesDir, diffsDir, versionsDir);
                if (latestVersion == null) {
                    System.exit(1);
                } else {
                    RDFDataMgr.write(System.out, latestVersion, RDFFormat.TURTLE_PRETTY);
                    System.exit(0);
                }
            }
        }

        usage();
        System.exit(1);
    }

    private static boolean checkDir(Path p) {
        if (Files.isDirectory(p)) {
            return true;
        } else {
            return err("%s is not a directory", p);
        }
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
        ADD, REPLACE, UPDATE
    }

    private static Pattern UPDATE_FILE_PATTERN = Pattern.compile(String.format(
            "^(?<date>\\d{4}-\\d{2}-\\d{2})(?:-\\d)?-(?<tag>[a-z-]+)-(?<op>%s)\\.(?<suffix>ttl|ru)$",
            Stream.of(Operation.values())
                    .map(String::valueOf)
                    .collect(Collectors.joining("|"))));

    private static PrefixMapping PREFIXES = PrefixMapping.Factory.create();
    static {
        // This is necessary because Jena doesn't maintain prefixes introduced by SPARQL UPDATEs.
        PREFIXES.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
        PREFIXES.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        PREFIXES.setNsPrefix("geojson", "https://purl.org/geojson/vocab#");
        PREFIXES.setNsPrefix("ncgaz", "http://n2t.net/ark:/39333/ncg/");
        PREFIXES.setNsPrefix("ncp", "http://n2t.net/ark:/39333/ncg/place/");
        PREFIXES.setNsPrefix("nct", "http://n2t.net/ark:/39333/ncg/type#");
        PREFIXES.setNsPrefix("ncv", "http://n2t.net/ark:/39333/ncg/vocab#");
        PREFIXES.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        PREFIXES.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        PREFIXES.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        PREFIXES.setNsPrefix("schema", "https://schema.org/");
        PREFIXES.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
        PREFIXES.setNsPrefix("skosxl", "http://www.w3.org/2008/05/skos-xl#");
        PREFIXES.setNsPrefix("wd", "http://www.wikidata.org/entity/");
        PREFIXES.setNsPrefix("wdt", "http://www.wikidata.org/prop/direct/");
        PREFIXES.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    }

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
                String suffix = m.group("suffix");

                String expectedSuffix = operation == Operation.UPDATE ? "ru" : "ttl";
                if (!suffix.equals(expectedSuffix)) {
                    err("%s has the wrong suffix; it should be ."
                            + expectedSuffix, updateFile);
                    return null;
                }

                // copy previous version to current version
                Model currentVersion = ModelFactory.createDefaultModel();
                currentVersion.add(previousVersion);

                String updateFilename = updateFile.toString();
                Model update = null;
                Diff diff = null;
                boolean ok = false;

                try {

                    switch (operation) {
                        case ADD:
                            update = RDFDataMgr.loadModel(updateFilename);
                            currentVersion.add(update);
                            break;

                        case REPLACE:
                            update = RDFDataMgr.loadModel(updateFilename);
                            ok = removeReplacedStatements(currentVersion, update);
                            if (!ok) {
                                err("Failed to remove statements replaced by %s", updateFile);
                                return null;
                            }
                            currentVersion.add(update);
                            break;

                        case UPDATE:
                            UpdateAction.readExecute(updateFilename, currentVersion);
                            break;

                        default:
                            err("Unknown operation: %s", updateFile);
                            return null;
                    }

                    diff = getDiff(previousVersion, currentVersion);

                    ok = write(currentVersion, versionsDir, "%s-%s.ttl", date, tag);
                    if (!ok) {
                        return null;
                    }

                    ok = write(diff.removed, diffsDir, "%s-%s/removed.ttl", date, tag);
                    if (!ok) {
                        return null;
                    }
                    ok = write(diff.added, diffsDir, "%s-%s/added.ttl", date, tag);
                    if (!ok) {
                        return null;
                    }

                    return currentVersion;

                } finally {
                    previousVersion.close();
                    if (update != null) {
                        update.close();
                    }
                    if (diff != null) {
                        diff.close();
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

    private static boolean removeReplacedStatements(Model currentVersion, Model update) {
        StmtIterator updateStmts = update.listStatements();
        while (updateStmts.hasNext()) {
            Statement updateStmt = updateStmts.next();
            Resource s = updateStmt.getSubject();
            Property p = updateStmt.getPredicate();

            if (s.isURIResource()) {
                currentVersion.remove(
                        currentVersion.listStatements(s, p, (RDFNode) null).toList());
            } else {
                return err("Blank subject node found in update");
            }
        }
        return true;
    }

    private static class Diff {
        final Model removed;
        final Model added;

        Diff(Model removed, Model added) {
            this.removed = removed;
            this.added = added;
        }

        void close() {
            this.removed.close();
            this.added.close();
        }
    }

    private static Diff getDiff(Model previousVersion, Model currentVersion) {
        List<Statement> remained = previousVersion.intersection(currentVersion)
            .listStatements().toList();

        Model removed = ModelFactory.createDefaultModel();
        removed.add(previousVersion);
        removed.remove(remained);

        Model added = ModelFactory.createDefaultModel();
        added.add(currentVersion);
        added.remove(remained);

        return new Diff(removed, added);
    }

    private static boolean write(Model m, Path dir, String rest, Date date, String tag) {
        if (m.isEmpty()) {
            return true;
        }
        m.setNsPrefixes(PREFIXES);
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
