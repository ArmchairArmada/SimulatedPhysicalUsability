package net.natewm.SimulatedPhysicalUsability.Project;

/**
 * Created by Nathan on 4/22/2017.
 */
public class TransitionDescription {
    private String destination;
    private int weight;
    private String selectionMethod;
    private String unavailableBehavior;

    public TransitionDescription() {
    }

    public TransitionDescription(String destination, int weight, String selectionMethod, String unavailableBehavior) {
        this.destination = destination;
        this.weight = weight;
        this.selectionMethod = selectionMethod;
        this.unavailableBehavior = unavailableBehavior;
    }

    public String getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public String getSelectionMethod() {
        return selectionMethod;
    }

    public String getUnavailableBehavior() {
        return unavailableBehavior;
    }
}
