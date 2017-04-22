package net.natewm.SimulatedPhysicalUsability.Project;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;
import net.natewm.SimulatedPhysicalUsability.Environment.Walls;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Nathan on 4/22/2017.
 */
public class ProjectData {
    ObjectMapper mapper = new ObjectMapper();

    private Walls walls = new Walls();
    private List<Location> locations = new ArrayList<>();
    private List<LocationType> locationTypes = new ArrayList<>();
    private Map<LocationType, List<Location>> locationMap = new HashMap<>();

    public ProjectData() {
    }

    public Walls getWalls() {
        return walls;
    }

    public void setWalls(Walls walls) {
        this.walls = walls;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<LocationType> getLocationTypes() {
        return locationTypes;
    }

    public void clear() {
        walls = new Walls();
        locations.clear();
        locationTypes.clear();
        locationMap.clear();
    }

    public void importProject(ProjectDescription projectDescription) {
        clear();
        Map<String, LocationType> locationTypeMap = new HashMap<>();

        for (WallDescription wall : projectDescription.getWalls()) {
            walls.addWall(new Walls.Wall(wall.getStartX(), wall.getStartY(), wall.getEndX(), wall.getEndY()));
        }

        LocationType locationType;
        for (LocationTypeDescription locType : projectDescription.getLocationTypes()) {
            locationType = new LocationType(locType.getName(), locType.getMinWait(), locType.getMaxWait(), locType.isStartOccupied(), locType.isEntrance(), locType.isExit());
            addLocationType(locationType);
            locationTypeMap.put(locationType.getName(), locationType);
        }

        for (LocationDescription loc : projectDescription.getLocations()) {
            addLocation(new Location(locationTypeMap.get(loc.getLocationType()), loc.getX(), loc.getY()));
        }
    }

    public ProjectDescription exportProject() {
        return new ProjectDescription(this);
    }

    public void loadProject(File file) throws IOException {
        importProject(mapper.readValue(file, ProjectDescription.class));
    }

    public void saveProject(File file) throws IOException {
        mapper.writeValue(file, exportProject());
    }

    public void addLocationType(LocationType locationType) {
        locationTypes.add(locationType);
        locationMap.put(locationType, new ArrayList<>());
    }

    public void removeLocationType(LocationType locationType) {
        locationTypes.remove(locationType);
        locationMap.remove(locationType);
    }

    public void addLocation(Location location) {
        locations.add(location);
        locationMap.get(location.getLocationType()).add(location);
    }

    public void removeLocation(Location location) {
        locations.remove(location);
        locationMap.get(location.getLocationType()).remove(location);
    }

    public List<Location> getLocationList(LocationType locationType) {
        return locationMap.get(locationType);
    }
}
