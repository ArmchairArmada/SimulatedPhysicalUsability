package net.natewm.SimulatedPhysicalUsability.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 4/22/2017.
 */
public class LocationTypeDescription {
    private String name;
    private float minWait;
    private float maxWait;
    private boolean startOccupied;
    private boolean entrance;
    private boolean exit;
    private float[] color;
    private List<TransitionDescription> transitions;

    public LocationTypeDescription() {
    }

    public LocationTypeDescription(String name, float minWait, float maxWait, boolean startOccupied, boolean entrance, boolean exit, float[] color) {
        this.name = name;
        this.minWait = minWait;
        this.maxWait = maxWait;
        this.startOccupied = startOccupied;
        this.entrance = entrance;
        this.exit = exit;
        this.color = color;
        transitions = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public float getMinWait() {
        return minWait;
    }

    public float getMaxWait() {
        return maxWait;
    }

    public boolean isStartOccupied() {
        return startOccupied;
    }

    public boolean isEntrance() {
        return entrance;
    }

    public boolean isExit() {
        return exit;
    }

    public List<TransitionDescription> getTransitions() {
        return transitions;
    }

    public void addTransition(TransitionDescription transitionDescription) {
        transitions.add(transitionDescription);
    }

    public float[] getColor() {
        return color;
    }
}
