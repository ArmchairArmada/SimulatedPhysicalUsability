package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Nathan on 1/3/2017.
 */
public class MeshDescription {
    List<String> geometry;
    //private String geometryLowRes = null;
    String material;

    public List<String> getGeometry() {
        return geometry;
    }

    //public String getGeometryLowRes() {
    //    return geometryLowRes;
    //}

    public String getMaterial() {
        return material;
    }

    public static MeshDescription loadFromJSON(ObjectMapper objectMapper, String filename) throws IOException {
        return objectMapper.readValue(new File(filename), MeshDescription.class);
    }
}
