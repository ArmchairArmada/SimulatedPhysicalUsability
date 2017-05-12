package net.natewm.SimulatedPhysicalUsability.Project;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;
import net.natewm.SimulatedPhysicalUsability.Environment.Walls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 1/29/2017.
 */
public class ProjectDescription {
    private List<WallDescription> walls;
    private List<LocationTypeDescription> locationTypes;
    private List<LocationDescription> locations;

    public ProjectDescription() {
    }

    public ProjectDescription(ProjectData projectData) {
        walls = new ArrayList<>();
        locationTypes = new ArrayList<>();
        locations = new ArrayList<>();

        for (Walls.Wall wall : projectData.getWalls().getWalls()) {
            walls.add(new WallDescription(wall.startX, wall.startY, wall.endX, wall.endY));
        }

        for (LocationType locationType : projectData.getLocationTypes()) {
            float[] color = {locationType.getColor().getRed()/255.0f, locationType.getColor().getGreen()/255.0f, locationType.getColor().getBlue()/255.0f};
            LocationTypeDescription locationTypeDescription = new LocationTypeDescription(locationType.getName(), locationType.getMinWaitTime(), locationType.getMaxWaitTime(), locationType.isStartOccupied(), locationType.isEntrance(), locationType.isExit(), color);
            for (LocationType.Transition transition : locationType.getTransitions()) {
                locationTypeDescription.addTransition(new TransitionDescription(transition.getDestination().getName(), transition.getWeight(), LocationType.getSelectionMethodString(transition.getSelectionMethod()), LocationType.getUnavailableBehaviorString(transition.getUnavailableBehavior())));
            }
            locationTypes.add(locationTypeDescription);
        }

        for (Location location : projectData.getLocations()) {
            locations.add(new LocationDescription(location.getLocationType().getName(), location.getX(), location.getY()));
        }
    }

    public List<WallDescription> getWalls() {
        return walls;
    }

    public List<LocationDescription> getLocations() {
        return locations;
    }

    public List<LocationTypeDescription> getLocationTypes() {
        return locationTypes;
    }

    public void saveToJSON(ObjectMapper mapper, String filename) throws IOException {
        mapper.writeValue(new File(filename), this);
    }

    public static ProjectDescription loadFromJSON(ObjectMapper mapper, String filename) throws IOException {
        return mapper.readValue(new File(filename), ProjectDescription.class);
    }
}
