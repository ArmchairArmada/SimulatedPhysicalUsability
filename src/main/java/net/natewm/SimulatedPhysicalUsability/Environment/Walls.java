package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.Rendering.Triangle;
import net.natewm.SimulatedPhysicalUsability.Resources.Geometry;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 1/18/2017.
 */
public class Walls {
    private static final float WALL_THICKNESS = 0.1f;
    private static final float WALL_HEIGHT = 0.5f;

    public class Wall {
        public float startX;
        public float startY;
        public float endX;
        public float endY;

        public Wall(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }


    private List<Wall> walls = new ArrayList<Wall>();


    public Walls() {
        walls.add(new Wall(0,0, 1, 0));
        walls.add(new Wall(0,0, 0, 1));
        //walls.add(new Wall(0,0, 1, 1));
    }


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
        float startX;
        float startY;
        float endX;
        float endY;
        int index;      // Vertex index
        Vector3f v0, v1, v2, v3, v4, v5, v6, v7;

        // TODO: These need to be computed to be parallel and perpendicular to sides of the wall.
        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f down = new Vector3f(0, -1, 0);
        Vector3f left;
        Vector3f right;
        Vector3f front;
        Vector3f back;

        Geometry geometry = new Geometry();
        geometry.startSubGeometry();

        for (Wall wall : walls) {
            offX = wall.endX - wall.startX;
            offY = wall.endY - wall.startY;
            distance = (float)Math.sqrt(offX*offX + offY*offY);

            paralX = offX / distance;
            paralY = offY / distance;
            perpX = -offY / distance;
            perpY = offX / distance;

            thickX = perpX * WALL_THICKNESS;
            thickY = perpY * WALL_THICKNESS;

            startX = wall.startX - paralX * WALL_THICKNESS;
            startY = wall.startY - paralY * WALL_THICKNESS;
            endX = wall.endX + paralX * WALL_THICKNESS;
            endY  = wall.endY + paralY * WALL_THICKNESS;

            left = new Vector3f(-perpX, 0, -perpY);
            right = new Vector3f(perpX, 0, perpY);
            front = new Vector3f(-paralX, 0, -paralY);
            back = new Vector3f(paralX, 0, paralY);

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

        geometry.finalizeSubGeometry();

        return geometry;
    }
}
