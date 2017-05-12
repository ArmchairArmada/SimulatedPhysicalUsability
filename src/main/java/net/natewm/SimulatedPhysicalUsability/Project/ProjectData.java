package net.natewm.SimulatedPhysicalUsability.Project;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;
import net.natewm.SimulatedPhysicalUsability.Environment.Walls;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by Nathan on 4/22/2017.
 */
public class ProjectData {
    private final ObjectMapper mapper = new ObjectMapper();

    private Walls walls = new Walls();
    private final List<Location> locations = new ArrayList<>();
    private final List<LocationType> locationTypes = new ArrayList<>();
    private final Map<LocationType, List<Location>> locationMap = new HashMap<>();

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
            locationType.setColor(new Color(locType.getColor()[0], locType.getColor()[1], locType.getColor()[2]));
            addLocationType(locationType);
            locationTypeMap.put(locationType.getName(), locationType);
        }

        LocationType.Transition transition;
        LocationType.SelectionMethod selectionMethod;
        LocationType.UnavailableBehavior unavailableBehavior;
        for (LocationTypeDescription locType : projectDescription.getLocationTypes()) {
            locationType = locationTypeMap.get(locType.getName());
            for (TransitionDescription transitionDescription : locType.getTransitions()) {
                selectionMethod = LocationType.getSelectionMethodFromString(transitionDescription.getSelectionMethod());
                unavailableBehavior = LocationType.getUnavailableBehaviorFromString(transitionDescription.getUnavailableBehavior());
                transition = new LocationType.Transition(locationTypeMap.get(transitionDescription.getDestination()), transitionDescription.getWeight(), selectionMethod, unavailableBehavior);
                locationType.addTransition(transition);
            }
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

    public void clearEnvironment() {
        walls = new Walls();
        locations.clear();
        locationMap.clear();
        for (LocationType locationType : locationTypes) {
            locationMap.put(locationType, new ArrayList<>());
        }
    }
}
