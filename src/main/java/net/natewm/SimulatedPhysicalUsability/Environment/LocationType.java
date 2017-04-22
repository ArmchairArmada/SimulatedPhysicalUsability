package net.natewm.SimulatedPhysicalUsability.Environment;

import com.sun.org.apache.regexp.internal.RE;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;
import net.natewm.SimulatedPhysicalUsability.Utils.ProbabilityChooser;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Nathan on 3/14/2017.
 */
public class LocationType {
    public enum SelectionMethod {
        NEAREST,
        RANDOM
    }

    public enum UnavailableBehavior  {
        REPICK,
        WAIT,
        QUEUE
    }

    public static class Transition {
        LocationType destination;
        int weight;
        SelectionMethod selectionMethod;
        UnavailableBehavior unavailableBehavior;

        public Transition(LocationType destination, int weight,
                   SelectionMethod selectionMethod, UnavailableBehavior unavailableBehavior) {
            this.destination = destination;
            this.weight = weight;
            this.selectionMethod = selectionMethod;
            this.unavailableBehavior = unavailableBehavior;
        }

        public LocationType getDestination() {
            return destination;
        }

        public int getWeight() {
            return weight;
        }

        public SelectionMethod getSelectionMethod() {
            return selectionMethod;
        }

        public UnavailableBehavior getUnavailableBehavior() {
            return unavailableBehavior;
        }
    }

    private String name = "";
    private ProbabilityChooser<Transition> transitions = new ProbabilityChooser<>();
    private float minWaitTime = 0.0f;
    private float maxWaitTime = 1.0f;
    private boolean entrance = false;
    private boolean exit = false;
    private boolean startOccupied = false;
    private Color color;

    public LocationType() {
    }

    public LocationType(String name, float minWaitTime, float maxWaitTime, boolean startOccupied, boolean entrance, boolean exit) {
        this.name = name;
        this.maxWaitTime = minWaitTime;
        this.maxWaitTime = maxWaitTime;
        this.startOccupied = startOccupied;
        this.entrance = entrance;
        this.exit = exit;
        this.color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
    }

    public static SelectionMethod getSelectionMethodFromString(String method) {
        switch (method) {
            case "Nearest":
                return SelectionMethod.NEAREST;

            case "Random":
                return SelectionMethod.RANDOM;

            default:
                // TODO: Error handling
                return SelectionMethod.RANDOM;
        }
    }

    public static UnavailableBehavior getUnavailableBehaviorFromString(String behavior) {
        switch (behavior) {
            case "Repick":
                return UnavailableBehavior.REPICK;

            case "Wait":
                return UnavailableBehavior.WAIT;

            case "Queue":
                return UnavailableBehavior.QUEUE;

            default:
                // TODO: Error Handling
                return UnavailableBehavior.REPICK;
        }
    }

    public static String getSelectionMethodString(SelectionMethod method) {
        switch (method) {
            case NEAREST:
                return "Nearest";

            case RANDOM:
                return "Random";

            default:
                // TODO: Error handling
                return "ERROR";
        }
    }

    public static String getUnavailableBehaviorString(UnavailableBehavior behavior) {
        switch (behavior) {
            case REPICK:
                return "Repick";

            case WAIT:
                return "Wait";

            case QUEUE:
                return "Queue";

            default:
                // TODO: Error Handling
                return "ERROR";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMinWaitTime() {
        return minWaitTime;
    }

    public void setMinWaitTime(float minWaitTime) {
        this.minWaitTime = minWaitTime;
    }

    public float getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(float maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public boolean isEntrance() {
        return entrance;
    }

    public void setEntrance(boolean value) {
        entrance = value;
    }

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public boolean isStartOccupied() {
        return startOccupied;
    }

    public void setStartOccupied(boolean startOccupied) {
        this.startOccupied = startOccupied;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addTransition(Transition transition) {
        transitions.insert(transition, transition.weight);
    }

    public void removeTransition(Transition transition) {
        transitions.remove(transition);
    }

    public Collection<Transition> getTransitions() {
        return transitions.getValues();
    }

    public void refreshProbabilities() {
        Collection<Transition> transitionCollection = getTransitions();
        transitions.clear();
        for (Transition transition : transitionCollection) {
            addTransition(transition);
        }
    }

    public Location randomTransition(ProjectData projectData) {
        Transition transition = transitions.getRandom();
        Location location;
        List<Location> locationList = projectData.getLocationList(transition.destination);
        switch (transition.selectionMethod) {
            case NEAREST:
                break;

            case RANDOM:
                switch (transition.unavailableBehavior) {
                    case REPICK:
                        location = locationList.get((int)(Math.random() * locationList.size()));
                        while (!location.isAvailable()) {
                            // TODO: Give up after failing a certain number of times.
                            location = locationList.get((int)(Math.random() * locationList.size()));
                        }
                        return location;

                    case WAIT:
                        break;

                    case QUEUE:
                        break;
                }
                break;
        }
        return null;
    }
}
