package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.Utils.ArrayUtils;


/**
 * Randomly genrates a maze of walls in the environment.
 */
public class MazeGenerator {
    /**
     * A cell for keeping track of maze generation data (visited and walls).
     */
    private class Cell {
        boolean visited = false;
        boolean topWall = true;
        boolean leftWall = true;
    }

    /**
     * A direction (how much x and y should change)
     */
    private class Dir {
        int dx;
        int dy;

        Dir(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private int width;
    private int height;
    private final float removeWallProbability;

    /**
     * Constructor for generating the maze.
     *
     * @param width                 Width of the maze in grid cells.
     * @param height                Height of the maze in grid cells.
     * @param removeWallProbability Probability of randomly removing walls to connect hallways.
     */
    public MazeGenerator(int width, int height, float removeWallProbability) {
        this.width = width;
        this.height = height;
        this.removeWallProbability = removeWallProbability;
    }

    /**
     * Generates the maze and returns walls describing its layout.
     *
     * @return Walls defining the maze's shape.
     */
    public Walls generate() {
        Walls walls = new Walls();
        Cell cell;
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        // Fill grid with blank cells.
        Cell[] cells = new Cell[width * height];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell();
        }

        // Recursively build out maze.
        recursiveMaze(cells, halfHeight, halfWidth, 0, 0);

        // Randomly remove walls to connect hallways.
        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                if (Math.random() < removeWallProbability)
                    ((Cell) cells[y * width + x]).topWall = false;

                if (Math.random() < removeWallProbability)
                    ((Cell) cells[y * width + x]).leftWall = false;
            }
        }

        // Add cell walls to walls list.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cell = cells[y * width + x];

                if (cell.topWall) {
                    walls.addWall(new Walls.Wall(x - halfWidth, y - halfHeight, x - halfWidth + 1, y - halfHeight));
                }

                if (cell.leftWall) {
                    walls.addWall(new Walls.Wall(x - halfWidth, y - halfHeight, x - halfWidth, y - halfHeight + 1));
                }
            }
        }

        // Add walls around the boundary of the maze.
        for (int x = 0; x < width; x++) {
            walls.addWall(new Walls.Wall(x - halfWidth, halfHeight, x - halfWidth + 1, halfHeight));
        }

        for (int y = 0; y < height; y++) {
            walls.addWall(new Walls.Wall(halfWidth, y - halfHeight, halfWidth, y - halfHeight + 1));
        }

        return walls;
    }

    /**
     * Recursively builds out mze.
     *
     * @param cells Grid cells
     * @param x     The current cell's x position.
     * @param y     The current cell's y position.
     * @param dx    The x direction taken to get to this cell.
     * @param dy    The y direction taken to get to this cell.
     */
    private void recursiveMaze(Cell[] cells, int x, int y, int dx, int dy) {
        // Check if the values given are within the boundary of the maze and the cell has not been visited yet.
        if (x < 0 || x >= width || y < 0 || y >= height || cells[y * width + x].visited)
            return;

        // Gets the current cell and previous cell.
        Cell cell = cells[y * width + x];
        Cell oldCell = cells[(y - dy) * width + x - dx];

        // Visit this cell.
        cell.visited = true;

        // Remove vertical wall between cells if moved horizontally.
        if (dx == -1) {
            oldCell.leftWall = false;
        } else if (dx == 1) {
            cell.leftWall = false;
        }

        // Remove horizontal wall between cells if moved vertically.
        if (dy == -1) {
            oldCell.topWall = false;
        } else if (dy == 1) {
            cell.topWall = false;
        }

        // Create derections that can be taken to arrive at the next cell.
        Dir[] dirs = new Dir[]{
                new Dir(-1, 0),
                new Dir(1, 0),
                new Dir(0, -1),
                new Dir(0, 1),
        };

        // Shuffle the directions so that the maze is actually a maze.
        ArrayUtils.shuffle(dirs);

        // Recursively build out the maze in these directions.
        for (Dir dir : dirs) {
            recursiveMaze(cells, x + dir.dx, y + dir.dy, dir.dx, dir.dy);
        }
    }
}
