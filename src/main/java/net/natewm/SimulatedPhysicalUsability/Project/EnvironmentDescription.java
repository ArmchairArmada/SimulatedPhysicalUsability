package net.natewm.SimulatedPhysicalUsability.Project;

import java.util.List;

/**
 * Created by Nathan on 1/29/2017.
 */
public class EnvironmentDescription {
    private List<WallDescription> walls;
    private List<LocationDescription> locations;

    public EnvironmentDescription() {
    }

    public EnvironmentDescription(List<WallDescription> walls, List<LocationDescription> locations) {
        this.walls = walls;
        this.locations = locations;
    }

    public List<WallDescription> getWalls() {
        return walls;
    }

    public List<LocationDescription> getLocations() {
        return locations;
    }
}
