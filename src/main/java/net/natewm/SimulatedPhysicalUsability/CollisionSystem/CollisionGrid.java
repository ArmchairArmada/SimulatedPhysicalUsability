package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

import net.natewm.SimulatedPhysicalUsability.Environment.Walls;

/**
 * Grid for checking collisions with walls.
 */
public class CollisionGrid {
    public static int HORIZONTAL = 1;   // Horizontal wall
    public static int VERTICAL = 2;     // Vertical wall


    /**
     * Cell for storing wall information.
     */
    private class Cell {
        boolean topWall = false;
        boolean leftWall = false;
    }



    private int width;      // Width of grid.
    private int height;     // Height of grid.
    private float minX;     // Minimum X value for grid bounds.
    private float minY;     // Minimum Y value for grid bounds.
    private float maxX;     // Maximum X value for grid bounds.
    private float maxY;     // Maximum Y value for grid bounds.
    private Object[] cells; // Wall cells


    /**
     * Default constructor.
     */
    public CollisionGrid() {
    }


    /**
     * Constructs collision grid from given wall information.
     *
     * @param walls Walls to place into collision grid.
     */
    public CollisionGrid(Walls walls) {
        reset(walls);
    }


    /**
     * Resets collision grid with given wall information.  This will makde the collision grid now have walls where the
     * given wall information has them defined.
     *
     * @param walls Walls to be placed into collision grid.
     */
    public void reset(Walls walls) {
        // Set bounds
        minX = walls.getMinX()-1;
        minY = walls.getMinY()-1;
        maxX = walls.getMaxX()+1;
        maxY = walls.getMaxY()+1;

        // Initialize array of wall objects.
        // TODO: Check if one should be added.
        width = (int)(Math.ceil(maxX) - Math.floor(minX));
        height = (int)(Math.ceil(maxY) - Math.floor(minY));
        cells = new Object[width * height];
        for (int i=0; i<width*height; i++) {
            cells[i] = new Cell();
        }

        // Add walls to collision grid.
        int x;
        int y;
        boolean horizontal;
        for (Walls.Wall wall : walls.getWalls()) {
            x = (int)(Math.floor(Math.min(wall.startX, wall.endX)-minX));
            y = (int)(Math.floor(Math.min(wall.startY, wall.endY)-minY));

            horizontal = Math.abs(wall.endX - wall.startX) > Math.abs(wall.endY - wall.startY);

            if (horizontal) {
                ((Cell)cells[y * width + x]).topWall = true;
            }
            else {
                ((Cell)cells[y * width + x]).leftWall = true;
            }
        }
    }


    /**
     * Gets width of collision grid.
     *
     * @return Width.
     */
    public int getWidth() {
        return width;
    }


    /**
     * Gets height of collision grid.
     *
     * @return Height.
     */
    public int getHeight() {
        return height;
    }


    /**
     * Gets minimum x bounding value.
     *
     * @return Minimum x bounding value.
     */
    public float getMinX() {
        return minX;
    }


    /**
     * Gets minimum y bounding value.
     *
     * @return Minimum y bounding value.
     */
    public float getMinY() {
        return minY;
    }


    /**
     * Gets Maximum x bouding value.
     *
     * @return Maximum x bounding value.
     */
    public float getMaxX() {
        return maxX;
    }


    /**
     * Gets maximum y bounding value.
     *
     * @return Maximum y bounding value.
     */
    public float getMaxY() {
        return maxY;
    }


    /**
     * Checks if a point is within bounds of the collision grid.
     *
     * @param x X coordinate of point.
     * @param y Y coordinate of point.
     * @return True if within bounds of grid, else false.
     */
    public boolean isInBounds(float x, float y) {
        return x >= minX && x < maxX && y >= minY && y < maxY;
    }


    /**
     * Checks if the cell at the given coordinate has a top wall.
     *
     * @param x X coordinate of cell.
     * @param y Y coordinate of cell.
     * @return True if cell at this position has a top wall.
     */
    public boolean hasTopWall(int x, int y) {
        return ((Cell)cells[y * width + x]).topWall;
    }


    /**
     * Checks if the cell at the given coordinate has a left wall.
     *
     * @param x X coordinate of cell.
     * @param y Y coordinate of cell.
     * @return True if cell at this position has a left wall.
     */
    public boolean hasLeftWall(int x, int y) {
        return ((Cell)cells[y * width + x]).leftWall;
    }


    /**
     * Calculates the index of a grid cell at a given coordinate.
     *
     * @param x X coordinate of point.
     * @param y Y coordinate of point.
     * @return The index of a cell which this point falls inside.
     */
    private int calcIndex(float x, float y) {
        return (int)(Math.floor(y-minY) * width + Math.floor(x-minX));
    }


    /**
     * Tests if a line segment from a start point to an end point intersects with a wall.
     *
     * @param startX Start X coordinate of line segment.
     * @param startY Start Y coordinate of line segment.
     * @param endX   End X coordinate of line segment.
     * @param endY   End Y coordinate of line segment.
     * @return True if line segment intersects wall, else false.
     */
    public int hitWall(float startX, float startY, float endX, float endY) {
        // TODO: Make more robust (arbitrary length lines).

        float x;
        float y;
        float ox;
        float oy;
        int dx;
        int dy;
        int wallsHit = 0;

        ox = startX;
        oy = startY;
        x = endX;
        y = endY;
        dx = (int)(Math.floor(x)) - (int)(Math.floor(ox));
        dy = (int)(Math.floor(y)) - (int)(Math.floor(oy));

        if (ox >= minX && ox < maxX && oy >= minY && oy < maxY
        && x >= minX && x < maxX && y >= minY && y < maxY) {
            if (dx < 0) {
                if (((Cell) cells[calcIndex(ox, oy)]).leftWall || ((Cell) cells[calcIndex(ox, y)]).leftWall) {
                    wallsHit |= VERTICAL;
                }
            } else if (dx > 0) {
                if(((Cell) cells[calcIndex(x, oy)]).leftWall || ((Cell) cells[calcIndex(x, y)]).leftWall) {
                    wallsHit |= VERTICAL;
                }
            }

            if (dy < 0) {
                if (((Cell) cells[calcIndex(ox, oy)]).topWall || ((Cell) cells[calcIndex(x, oy)]).topWall) {
                    wallsHit |= HORIZONTAL;
                }
            } else if (dy > 0) {
                if (((Cell) cells[calcIndex(ox, y)]).topWall || ((Cell) cells[calcIndex(x, y)]).topWall) {
                    wallsHit |= HORIZONTAL;
                }
            }

            if (wallsHit > 0) {
                return wallsHit;
            }
        }

        return wallsHit;
    }
}
