package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple preprocessor for GLSL shader files to be able to include other files.
 */
public class ShaderPreprocessor {
    private static final Logger LOGGER = Logger.getLogger(ShaderPreprocessor.class.getName());

    /**
     * Loads a shader from file with preprocessing applied
     *
     * @param dir      Directory of the file
     * @param filename File name
     * @return Text resulting from preprocessing the file
     * @throws IOException Thrown if file cannot be loaded
     */
    public static String[] load(String dir, String filename) throws IOException {
        List<String> lines = processFile(dir, filename);
        return lines.toArray(new String[0]);
    }

    /**
     * Performs the preprocessing on a file.
     *
     * @param dir      Directory the file is in
     * @param filename File to load with preprocessing
     * @return List of strings for preprocessed file
     * @throws IOException Thrown if file cannot be loaded
     */
    private static List<String> processFile(String dir, String filename) throws IOException {
        List<String> fileLines = Files.readAllLines(Paths.get(dir+filename), StandardCharsets.UTF_8);
        fixLineEndings(fileLines);
        return preprocess(dir, fileLines);
    }

    /**
     * Since line endings get stripped, new lines get added back on.
     *
     * @param lines Lines to have line endings appended onto
     */
    private static void fixLineEndings(List<String> lines) {
        for (int i=0; i<lines.size(); i++) {
            lines.set(i, lines.get(i).concat("\r\n"));
        }
    }

    /**
     * Preprocesses lines from a file.  Currently the only command added is to include other files.
     *
     * @param dir   Directory of the file
     * @param lines Lines of text to preprocess
     * @return List of new lines after preprocessing
     * @throws IOException Thrown if included file cannot be preprocessed
     */
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
}
