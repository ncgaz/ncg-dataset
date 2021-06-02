package org.ncgazetteer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.jsonld.http.media.MediaType;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;

public class FrameDataset {

    public static void main(String[] args) {
        if (args.length == 2) {
            Path datasetPath = Paths.get(args[0]);
            Path framePath = Paths.get(args[1]);

            boolean ok = false;
            ok = checkPath(datasetPath);
            ok = checkPath(framePath);

            if (ok) {
                ok = frame(datasetPath, framePath);
                if (ok) {
                    System.exit(0);
                } else {
                    System.exit(1);
                }
            }
        }

        usage();
        System.exit(1);
    }

    private static boolean checkPath(Path p) {
        if (Files.isReadable(p)) {
            return true;
        } else {
            return err("Cannot read %s", p);
        }
    }

    private static JsonWriterFactory Pretty = Json.createWriterFactory(
                Map.of(JsonGenerator.PRETTY_PRINTING, true));

    private static boolean frame(Path datasetPath, Path framePath) {
        try (BufferedReader datasetReader = Files.newBufferedReader(datasetPath);
                BufferedReader frameReader = Files.newBufferedReader(framePath)) {
            Document rdf = RdfDocument.of(MediaType.N_QUADS, datasetReader);
            Document jsonld = JsonDocument.of(JsonLd.fromRdf(rdf).get());
            //JsonStructure js = jsonld.getJsonContent().get();
            //Pretty.createWriter(System.out).write(js);

            //Document jsonld = JsonDocument.of(datasetReader);
            Document jsonldFrame = JsonDocument.of(frameReader);
            JsonObject results = JsonLd.frame(jsonld, jsonldFrame).get();

            Pretty.createWriter(System.out).writeObject(results);

            return true;

        } catch (IOException | JsonLdError e) {
            return err("Failed to frame dataset using %s: %s", framePath, e);
        }
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
        System.err.println("  frame-dataset dataset frame");
        System.err.println();
        System.err.println("  `dataset` should be an RDF dataset");
        System.err.println();
        System.err.println("  `frame` should be a JSON-LD frame");
        System.err.println();
    }
}
