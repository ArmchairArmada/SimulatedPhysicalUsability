package net.natewm.SimulatedPhysicalUsability.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Nathan on 1/12/2017.
 */
public class ShaderPreprocessor {
    private static final Logger LOGGER = Logger.getLogger(ShaderPreprocessor.class.getName());

    public static String[] load(String dir, String filename) throws IOException {
        List<String> lines = processFile(dir, filename);
        return lines.toArray(new String[0]);
    }

    private static List<String> processFile(String dir, String filename) throws IOException {
        List<String> fileLines = Files.readAllLines(Paths.get(dir+filename), StandardCharsets.UTF_8);
        fixLineEndings(fileLines);
        return preprocess(dir, fileLines);
    }

    private static void fixLineEndings(List<String> lines) {
        for (int i=0; i<lines.size(); i++) {
            lines.set(i, lines.get(i).concat("\r\n"));
        }
    }

    public static List<String> preprocess(String dir, List<String> lines) throws IOException {
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            // To not be confused with GLSL's preprocessor, I will use % instead of #
            if (line.startsWith("%")) {
                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "%include": {
                        newLines.addAll(processFile(dir, parts[1]));
                        break;
                    }

                    default:
                        LOGGER.log(Level.SEVERE, "GLSL preprocess error: ", line);
                }
            }
            else {
                newLines.add(line);
            }
        }

        return newLines;
    }

    private static List<String> loadLines(String filename) throws IOException {
        List<String> lines;

        lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
        for (int i=0; i<lines.size(); i++) {
            lines.set(i, lines.get(i).concat("\r\n"));
        }

        return lines;
    }
}
