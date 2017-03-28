package net.natewm.SimulatedPhysicalUsability.Project;

/**
 * Created by Nathan on 1/29/2017.
 */
public class LocationDescription {
    private String locationType;
    private float x;
    private float y;

    public LocationDescription(String locationType, float x, float y) {
        this.locationType = locationType;
        this.x = x;
        this.y = y;
    }

    public String getLocationType() {
        return locationType;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
