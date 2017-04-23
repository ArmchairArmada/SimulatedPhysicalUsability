package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.Project.LocationDescription;
import net.natewm.SimulatedPhysicalUsability.Simulation.Agent;

/**
 * Created by Nathan on 3/14/2017.
 */
public class Location {
    private LocationType locationType;
    private float x;
    private float y;
    private Agent occupiedBy = null;
    private int navGridId;

    public Location(LocationType locationType, float x, float y) {
        this.locationType = locationType;
        this.x = x;
        this.y = y;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public boolean isExit() {
        return locationType.isExit();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void occupy(Agent agent) {
        occupiedBy = agent;
    }

    public void leave() {
        occupiedBy = null;
    }

    public boolean isAvailable() {
        return occupiedBy == null;
    }

    public boolean isInRange(float x, float y) {
        return x > this.x && x <= this.x + 1.0f && y > this.y && y <= this.y + 1.0f;
    }

    public void setNavGridId(int navGridId) {
        this.navGridId = navGridId;
    }

    public int getNavGridId() {
        return navGridId;
    }
}
