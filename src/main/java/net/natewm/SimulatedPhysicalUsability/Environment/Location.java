package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.Simulation.Agent;

/**
 * A location in the environment where agents can enter, travel to, and exit from.
 */
public class Location {
    private final LocationType locationType;
    private final float x;
    private final float y;
    private Agent occupiedBy = null;
    private int navGridId;

    /**
     * Constructs a location.
     *
     * @param locationType Type of this location
     * @param x            X position of location
     * @param y            Y position of location
     */
    public Location(LocationType locationType, float x, float y) {
        this.locationType = locationType;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the type of the location.
     *
     * @return A LocationType
     */
    public LocationType getLocationType() {
        return locationType;
    }

    /**
     * Checks if this location is an exit.
     *
     * @return True if it is an exit, else false
     */
    public boolean isExit() {
        return locationType.isExit();
    }

    /**
     * Gets the X position of this location.
     *
     * @return Location's X position
     */
    public float getX() {
        return x;
    }

    /**
     * Gets the Y position of this Location.
     *
     * @return Location's Y position
     */
    public float getY() {
        return y;
    }

    /**
     * Makes the location be occupied by the specified agent.
     *
     * @param agent Agent occupying the location.
     */
    public void occupy(Agent agent) {
        occupiedBy = agent;
    }

    /**
     * Makes an agent leave the location.
     */
    public void leave() {
        occupiedBy = null;
    }

    /**
     * Checks if the location is available (not occupied by any agents).
     *
     * @return True if the location is available (not occupied)
     */
    public boolean isAvailable() {
        return occupiedBy == null;
    }

    /**
     * Checks if a position is within range of this location (in the same ground's square meter area)
     *
     * @param x X position to test
     * @param y Y position to test
     * @return True if the agent is within range of the location
     */
    public boolean isInRange(float x, float y) {
        return x > this.x && x <= this.x + 1.0f && y > this.y && y <= this.y + 1.0f;
    }

    /**
     * Sets the navigation grid ID for this location, which has vectors leading toward it.
     *
     * @param navGridId The ID of the navigation grid data
     */
    public void setNavGridId(int navGridId) {
        this.navGridId = navGridId;
    }

    /**
     * Gets the navigation grid ID for this location.
     *
     * @return Navigation grid ID
     */
    public int getNavGridId() {
        return navGridId;
    }
}
