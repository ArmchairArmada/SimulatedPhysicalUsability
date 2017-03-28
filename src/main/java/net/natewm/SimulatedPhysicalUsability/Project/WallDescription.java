package net.natewm.SimulatedPhysicalUsability.Project;

/**
 * Created by Nathan on 1/29/2017.
 */
public class WallDescription {
    private float startX;
    private float startY;
    private float endX;
    private float endY;

    public WallDescription(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getEndX() {
        return endX;
    }

    public float getEndY() {
        return endY;
    }
}
