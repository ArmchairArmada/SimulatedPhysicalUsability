package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Triangle;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Geometry;
import net.natewm.SimulatedPhysicalUsability.Project.WallDescription;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the geometry for the walls in the environment.
 */
public class Walls {
    private static final float WALL_THICKNESS = 0.1f;
    private static final float WALL_HEIGHT = 0.25f;

    /**
     * Information about the wall's start and end position.
     */
    public static class Wall {
        public final float startX;
        public final float startY;
        public final float endX;
        public final float endY;

        public Wall(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }


    private final List<Wall> walls = new ArrayList<>();
    private float minX = Float.MAX_VALUE;
    private float minY = Float.MAX_VALUE;
    private float maxX = Float.MIN_VALUE;
    private float maxY = Float.MIN_VALUE;


    /**
     * Default constructor (doesn't need to do anything.)
     */
    public Walls() {
    }

    public List<WallDescription> exportWalls() {
        ArrayList<WallDescription> wallDescriptions = new ArrayList<>();
        for (Wall wall: walls) {
            wallDescriptions.add(new WallDescription(wall.startX, wall.startY, wall.endX, wall.endY));
        }
        return wallDescriptions;
    }


    /**
     * Adds a wall to the list of walls.
     *
     * @param wall Wall to add
     */
    public void addWall(Wall wall) {
        walls.add(wall);

        minX = Math.min(minX, wall.startX);
        minX = Math.min(minX, wall.endX);

        minY = Math.min(minY, wall.startY);
        minY = Math.min(minY, wall.endY);

        maxX = Math.max(maxX, wall.startX);
        maxX = Math.max(maxX, wall.endX);

        maxY = Math.max(maxY, wall.startY);
        maxY = Math.max(maxY, wall.endY);
    }

    /**
     * Gets the list of walls.
     *
     * @return List of walls.
     */
    public List<Wall> getWalls() {
        return walls;
    }


    /**
     * Gets the minimum X value for the bounding rectangle of the environment.
     *
     * @return Minimum X value.
     */
    public float getMinX() {
        return minX;
    }


    /**
     * Gets the minimum Y value for the bounding rectangle of the environment.
     *
     * @return Minimum Y value.
     */
    public float getMinY() {
        return minY;
    }


    /**
     * Gets the maximum X value for the bounding rectangle of the environment.
     *
     * @return Maximum X value.
     */
    public float getMaxX() {
        return maxX;
    }


    /**
     * Gets the maximum Y value for the bounding rectangle of the environment.
     *
     * @return Maximum Y value.
     */
    public float getMaxY() {
        return maxY;
    }


    /**
     * Generates geometry from the list of walls.
     *
     * @return Geometry of the walls.
     */
    public Geometry generateGeometry() {
        /*
            Notes on wall vertex order:
                   Back
                  6----5
              Top/|   /|
            Left/ |  / |
               /  7-/--4
              /  / /  /
             /  / /  /
            2----1  /Right
            | /  | /Bottom
            |/   |/
            3----0
            Front
         */
        float offX;     // Offset in X direction (endX - startX)
        float offY;     // Offset in Y direction (endY - startY)
        float distance; // Length of the wall
        float paralX;   // Parallel in local X direction
        float paralY;   // Parallel in local Y direction
        float perpX;    // Perpendicular in local X direction
        float perpY;    // Perpendicular in local Y direction
        float thickX;   // Wall thickness in local X direction
        float thickY;   // Wall thickness in local Y direction
        float startX;   // Starting X position.
        float startY;   // Starting Y position.
        float endX;     // Ending X position.
        float endY;     // Ending Y position.
        int index;      // Vertex index
        Vector3f v0, v1, v2, v3, v4, v5, v6, v7;    // Vertices

        Vector3f up = new Vector3f(0, 1, 0);    // Vector pointing up from top of wall.
        Vector3f down = new Vector3f(0, -1, 0); // Vector pointing down from bottom of wall.
        Vector3f left;  // Vector pointing from the left side of the wall.
        Vector3f right; // Vector pointing from the right side of the wall.
        Vector3f front; // Vector pointing from the front of the wall.
        Vector3f back;  // Vector pointing from the back of the wall.

        Geometry geometry = new Geometry(); // Geometry that we will be generating.
        geometry.startSubGeometry();        // This will only have one sub-geometry.

        // Build out the walls.
        for (Wall wall : walls) {
            // Get the offsets and distances.
            offX = wall.endX - wall.startX;
            offY = wall.endY - wall.startY;
            distance = (float)Math.sqrt(offX*offX + offY*offY);

            // Get parallel and perpendicular vector components.
            paralX = offX / distance;
            paralY = offY / distance;
            perpX = -offY / distance;
            perpY = offX / distance;

            // Compute the wall thickness for each vector component.
            thickX = perpX * WALL_THICKNESS;
            thickY = perpY * WALL_THICKNESS;

            // Extend the walls a little to include the thickness (corresponding to actual vertex positions).
            startX = wall.startX - paralX * WALL_THICKNESS;
            startY = wall.startY - paralY * WALL_THICKNESS;
            endX = wall.endX + paralX * WALL_THICKNESS;
            endY  = wall.endY + paralY * WALL_THICKNESS;

            // Compute vectors relative to the wall orientation.
            left = new Vector3f(-perpX, 0, -perpY);
            right = new Vector3f(perpX, 0, perpY);
            front = new Vector3f(-paralX, 0, -paralY);
            back = new Vector3f(paralX, 0, paralY);

            // Compute the positions of the corner vertices.
            // To have flat shading, vertices need to be each used by three faces, so it needs duplication.
            v0 = new Vector3f(startX + thickX, 0f, startY + thickY);
            v1 = new Vector3f(startX + thickX, WALL_HEIGHT, startY + thickY);
            v2 = new Vector3f(startX - thickX, WALL_HEIGHT, startY - thickY);
            v3 = new Vector3f(startX - thickX, 0f, startY - thickY);

            v4 = new Vector3f(endX + thickX, 0f, endY + thickY);
            v5 = new Vector3f(endX + thickX, WALL_HEIGHT, endY + thickY);
            v6 = new Vector3f(endX - thickX, WALL_HEIGHT, endY - thickY);
            v7 = new Vector3f(endX - thickX, 0f, endY - thickY);

            // Front
            index = geometry.vertexCount();

            geometry.addVertex(v0);
            geometry.addVertex(v1);
            geometry.addVertex(v2);
            geometry.addVertex(v3);

            geometry.addNormal(front);
            geometry.addNormal(front);
            geometry.addNormal(front);
            geometry.addNormal(front);

            geometry.addTriangle(new Triangle(index, index+1, index+2));
            geometry.addTriangle(new Triangle(index, index+2, index+3));

            // Right
            index = geometry.vertexCount();

            geometry.addVertex(v0);
            geometry.addVertex(v4);
            geometry.addVertex(v5);
            geometry.addVertex(v1);

            geometry.addNormal(right);
            geometry.addNormal(right);
            geometry.addNormal(right);
            geometry.addNormal(right);

            geometry.addTriangle(new Triangle(index, index+1, index+2));
            geometry.addTriangle(new Triangle(index, index+2, index+3));

            // Top
            index = geometry.vertexCount();

            geometry.addVertex(v1);
            geometry.addVertex(v5);
            geometry.addVertex(v6);
            geometry.addVertex(v2);

            geometry.addNormal(up);
            geometry.addNormal(up);
            geometry.addNormal(up);
            geometry.addNormal(up);

            geometry.addTriangle(new Triangle(index, index+1, index+2));
            geometry.addTriangle(new Triangle(index, index+2, index+3));

            // Left
            index = geometry.vertexCount();

            geometry.addVertex(v3);
            geometry.addVertex(v2);
            geometry.addVertex(v6);
            geometry.addVertex(v7);

            geometry.addNormal(left);
            geometry.addNormal(left);
            geometry.addNormal(left);
            geometry.addNormal(left);

            geometry.addTriangle(new Triangle(index, index+1, index+2));
            geometry.addTriangle(new Triangle(index, index+2, index+3));

            // Bottom (Not visible)
            /*
            index = geometry.vertexCount();

            geometry.addVertex(v0);
            geometry.addVertex(v3);
            geometry.addVertex(v7);
            geometry.addVertex(v4);

            geometry.addNormal(down);
            geometry.addNormal(down);
            geometry.addNormal(down);
            geometry.addNormal(down);

            geometry.addTriangle(new Triangle(index, index+1, index+2));
            geometry.addTriangle(new Triangle(index, index+2, index+3));
            */

            // Back
            index = geometry.vertexCount();

            geometry.addVertex(v4);
            geometry.addVertex(v7);
            geometry.addVertex(v6);
            geometry.addVertex(v5);

            geometry.addNormal(back);
            geometry.addNormal(back);
            geometry.addNormal(back);
            geometry.addNormal(back);

            geometry.addTriangle(new Triangle(index, index+1, index+2));
            geometry.addTriangle(new Triangle(index, index+2, index+3));
        }

        // Finalize the geometry and return.
        geometry.finalizeSubGeometry();

        return geometry;
    }
}
