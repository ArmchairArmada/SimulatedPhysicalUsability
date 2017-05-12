package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Description of a material's properties, which can be loaded from a JSON file.
 */
public class MaterialDescription {
    /**
     * Material properties
     */
    public class Properties {
        private final List<String> textures = null;
        private final List<Double> diffuseColor = null;
        private final List<Double> specularColor = null;
        private final Double specularPower = null;
        private final Double specularIntensity = null;

        public List<String> getTextures() {
            return textures;
        }

        public List<Double> getDiffuseColor() {
            return diffuseColor;
        }

        public List<Double> getSpecularColor() {
            return specularColor;
        }

        public Double getSpecularPower() {
            return specularPower;
        }

        public Double getSpecularIntensity() {
            return specularIntensity;
        }
    }

    private final String vertexShader = null;
    private final String fragmentShader = null;
    private final Properties properties = null;

    /**
     * Gets the vertex shader filename.
     *
     * @return Vertex shader file name
     */
    public String getVertexShader() {
        return vertexShader;
    }

    /**
     * Gets the fragmetn shader filename
     *
     * @return Fragment shader file name
     */
    public String getFragmentShader() {
        return fragmentShader;
    }

    /**
     * Gets the properties describing the material.
     *
     * @return Material's properites
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Loads a MaterialDescription from a JSON file.
     *
     * @param mapper   Jackson JSON mapper
     * @param filename JSON filename
     * @return A material description
     * @throws IOException Throws exception if file cannot be loaded
     */
    public static MaterialDescription loadFromJSON(ObjectMapper mapper, String filename) throws IOException {
        return mapper.readValue(new File(filename), MaterialDescription.class);
    }
}
