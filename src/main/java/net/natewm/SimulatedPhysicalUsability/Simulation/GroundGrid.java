package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.Information.FloatGrid;
import org.joml.Vector3f;

/**
 * Created by Nathan on 1/11/2017.
 */
public class GroundGrid {
    FloatGrid[] floatGrids = null;
    int width;
    int height;
    int gridWidth;
    int gridHeight;
    int floorWidth;
    int floorHeight;
    float offsetX;
    float offsetY;

    public GroundGrid(int width, int height, int gridWidth, int gridHeight) {
        this.width = width;
        this.height = height;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.floorWidth = width * gridWidth;
        this.floorHeight = height * gridHeight;
        this.offsetX = -floorWidth/2.0f;
        this.offsetY = -floorHeight/2.0f;

        floatGrids = new FloatGrid[width * height];
    }

    private int toGridX(float x) {
        return (int)((x - offsetX) / width);
    }

    private int toGridY(float y) {
        return (int)((y - offsetY) / height);
    }

    private int toCellX(int gridX, float x) {
        return (int)(x - offsetX - gridX * gridWidth);
    }

    private int toCellY(int gridY, float y) {
        return (int)(y - offsetY - gridY * gridHeight);
    }

    private FloatGrid getGrid(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return null;

        FloatGrid grid = floatGrids[y * width + x];

        if (grid == null) {
            grid = new FloatGrid(gridWidth, gridHeight);
            floatGrids[y * width + x] = grid;
        }

        return grid;
    }

    public void add(Vector3f position, float value) {
        int x = toGridX(position.x);
        int y = toGridY(position.z);

        FloatGrid grid = getGrid(x, y);

        if (grid != null) {
            int cellX = toCellX(x, position.x);
            int cellY = toCellY(y, position.y);

            grid.set(cellX, cellY, grid.get(cellX, cellY) + value);
        }
    }

    public void set(Vector3f position, float value) {
        int x = toGridX(position.x);
        int y = toGridY(position.z);

        FloatGrid grid = getGrid(x, y);

        if (grid != null) {
            int cellX = toCellX(x, position.x);
            int cellY = toCellY(y, position.y);

            grid.set(cellX, cellY, value);
        }
    }

    public float get(Vector3f position, float value) {
        int x = toGridX(position.x);
        int y = toGridY(position.z);

        FloatGrid grid = getGrid(x, y);

        if (grid != null) {
            int cellX = toCellX(x, position.x);
            int cellY = toCellY(y, position.y);

            return grid.get(cellX, cellY);
        }

        return 0.0f;
    }
}
