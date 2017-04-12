package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.Utils.ProbabilityChooser;

import java.util.ArrayList;
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
    }

    private String name;
    private ProbabilityChooser<Transition> transitions = new ProbabilityChooser<>();
    private float minWaitTime = 0.0f;
    private float maxWaitTiem = 1.0f;
    private boolean exit = false;

    public LocationType(String name) {
        this.name = name;
        exit = name.equals("exit") || name.equals("emergency exit");
    }

    public String getName() {
        return name;
    }

    public boolean isExit() {
        return exit;
    }

    public void addTransition(Transition transition) {
        transitions.insert(transition, transition.weight);
    }

    public Location randomTransition(Environment environment) {
        Transition transition = transitions.getRandom();
        Location location;
        List<Location> locationList = environment.getLocations(transition.destination.getName());
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
