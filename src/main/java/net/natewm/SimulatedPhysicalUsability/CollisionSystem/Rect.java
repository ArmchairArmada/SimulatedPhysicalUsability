package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

/**
 * Created by Nathan on 1/22/2017.
 */
public class Rect {
    public float x;
    public float y;
    public float width;
    public float height;

    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isOverlapping(Rect rect) {
        if ((x + width) < rect.x || x > (rect.x + rect.width))
            return false;

        if ((y + height) < rect.y || y > (rect.y + rect.height))
            return false;

        return true;
    }

    public boolean isInside(float x, float y) {
        return this.x <= x && this.y <= y && (this.x + width) >= x && (this.y + height) >= y;
    }
}
