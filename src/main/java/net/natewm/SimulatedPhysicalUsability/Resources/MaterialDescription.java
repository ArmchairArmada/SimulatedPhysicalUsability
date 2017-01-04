package net.natewm.SimulatedPhysicalUsability.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 1/3/2017.
 */
public class MaterialDescription {
    public class Properties {
        private List<String> textures = null;
        private List<Double> diffuseColor = null;
        private List<Double> specularColor = null;
        private Double specularPower = null;
        private Double specularIntensity = null;

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

    private String vertexShader = null;
    private String fragmentShader = null;
    private Properties properties = null;

    public String getVertexShader() {
        return vertexShader;
    }

    public String getFragmentShader() {
        return fragmentShader;
    }

    public Properties getProperties() {
        return properties;
    }

    public static MaterialDescription loadFromJSON(ObjectMapper mapper, String filename) throws IOException {
        return mapper.readValue(new File(filename), MaterialDescription.class);
    }
}
