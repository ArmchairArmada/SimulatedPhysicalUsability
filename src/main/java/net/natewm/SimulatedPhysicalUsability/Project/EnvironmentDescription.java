package net.natewm.SimulatedPhysicalUsability.Project;

import java.util.List;

/**
 * Created by Nathan on 1/29/2017.
 */
public class EnvironmentDescription {
    private List<WallDescription> wallDescriptions;
    private List<LocationDescription> locationDescriptions;

    public EnvironmentDescription(List<WallDescription> wallDescriptions, List<LocationDescription> locationDescriptions) {
        this.wallDescriptions = wallDescriptions;
        this.locationDescriptions = locationDescriptions;
    }

    public List<WallDescription> getWalls() {
        return wallDescriptions;
    }

    public List<LocationDescription> getLocations() {
        return locationDescriptions;
    }
}
