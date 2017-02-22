package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

/**
 * Rectangle to use for performing spacial queries.
 */
public class Rect {
    public float x;         // Horizontal position of rectangle.
    public float y;         // Vertical position of rectangle.
    public float width;     // Width of rectangle.
    public float height;    // Height of rectangle.


    /**
     * Constructor for creating rectangle.
     *
     * @param x      Horizontal position of rectangle.
     * @param y      Vertical position of rectangle.
     * @param width  Width of rectangle.
     * @param height Height of rectangle.
     */
    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    /**
     * Get the x position of rectangle.
     *
     * @return X position.
     */
    public float getX() {
        return x;
    }


    /**
     * Get the y position of the rectangle.
     *
     * @return Y position.
     */
    public float getY() {
        return y;
    }


    /**
     * Gets the width of the rectangle.
     *
     * @return Width of rectangle.
     */
    public float getWidth() {
        return width;
    }


    /**
     * Gets the height of the rectangle.
     *
     * @return Height of rectangle.
     */
    public float getHeight() {
        return height;
    }


    /**
     * Checks if this rectangle overlaps with a given rectangle.
     *
     * @param rect Rectangle to check if overlapping.
     * @return True if the two rectangles overlap, else false.
     */
    public boolean isOverlapping(Rect rect) {
        if ((x + width) < rect.x || x > (rect.x + rect.width))
            return false;

        if ((y + height) < rect.y || y > (rect.y + rect.height))
            return false;

        return true;
    }


    /**
     * Checks if this rectangle fully contains a given rectangle.
     *
     * @param rect Rectangle to test is contained.
     * @return True if given rectangle is fully contained within this rectangle.
     */
    public boolean contains(Rect rect) {
        return x <= rect.x && y <= rect.y && x+width > rect.x+rect.width && y+height > rect.y+rect.height;
    }


    /**
     * Checks if a point is inside this rectangle.
     *
     * @param x X coordinate of point.
     * @param y Y coordinate of point.
     * @return True if point is inside rectangle, else false.
     */
    public boolean isInside(float x, float y) {
        return this.x <= x && this.y <= y && (this.x + width) >= x && (this.y + height) >= y;
    }
}
