package net.natewm.SimulatedPhysicalUsability.Project;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nathan on 1/29/2017.
 */
public class ProjectDescription {
    private EnvironmentDescription environmentDescription;

    public EnvironmentDescription getEnvironment() {
        return environmentDescription;
    }

    public void saveToJSON(ObjectMapper mapper, String filename) throws IOException {
        mapper.writeValue(new File(filename), this);
    }

    public static ProjectDescription loadFromJSON(ObjectMapper mapper, String filename) throws IOException {
        return mapper.readValue(new File(filename), ProjectDescription.class);
    }
}
