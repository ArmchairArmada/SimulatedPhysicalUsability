package net.natewm.SimulatedPhysicalUsability.Project;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nathan on 1/29/2017.
 */
public class EnvironmentDescription {
    private List<WallDescription> wallDescriptions;
    private List<LocationDescription> locationDescriptions;

    public List<WallDescription> getWalls() {
        return wallDescriptions;
    }

    public List<LocationDescription> getLocations() {
        return locationDescriptions;
    }
}
