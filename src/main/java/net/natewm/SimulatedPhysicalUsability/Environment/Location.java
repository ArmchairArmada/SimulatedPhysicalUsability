package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.Navigation.NavigationGrid;

/**
 * Created by Nathan on 3/14/2017.
 */
public class Location {
    private LocationType locationType;
    private float x;
    private float y;
    private int navGridId;

    public Location(LocationType locationType, float x, float y) {
        this.locationType = locationType;
        this.x = x;
        this.y = y;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setNavGridId(int navGridId) {
        this.navGridId = navGridId;
    }

    public int getNavGridId() {
        return navGridId;
    }
}
