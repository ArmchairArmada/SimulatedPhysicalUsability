package net.natewm.SimulatedPhysicalUsability.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nathan on 1/3/2017.
 */
public class MeshDescription {
    private String geometry;
    private String material;

    public String getGeometry() {
        return geometry;
    }

    public String getMaterial() {
        return material;
    }

    public static MeshDescription loadFromJSON(ObjectMapper objectMapper, String filename) throws IOException {
        return objectMapper.readValue(new File(filename), MeshDescription.class);
    }
}
