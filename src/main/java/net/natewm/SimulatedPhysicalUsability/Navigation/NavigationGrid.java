package net.natewm.SimulatedPhysicalUsability.Navigation;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.Utils.ArrayUtils;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Grid of navigation vectors.
 */
public class NavigationGrid {
    /**
     * Job used for concurrently generating grids of navigation data.
     */
    private class Job {
        final int x;
        final int y;
        final Vector2f[] grid;

        Job(int x, int y, Vector2f[] grid) {
            this.x = x;
            this.y = y;
            this.grid = grid;
        }
    }

    /**
     * Node used for building out navigation information
     */
    private class Node {
        final int x;
        final int y;
        final int fromX;
        final int fromY;
        final float distance;

        public Node(int x, int y, int fromX, int fromY, float distance) {
            this.x = x;
            this.y = y;
            this.fromX = fromX;
            this.fromY = fromY;
            this.distance = distance;
        }
    }

    /**
     * Compares nodes by their distance to their goal
     */
    private class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node x, Node y) {
            if (x.distance < y.distance)
                return -1;
            else if (x.distance > y.distance)
                return 1;
            return 0;
        }
    }

    private static final int[] dx = {0, 0, -1, 1, -1, -1, 1, 1};
    private static final int[] dy = {-1, 1, 0, 0, -1, 1, -1, 1};

    private final int width;
    private final int height;
    private final float minX;
    private final float minY;
    private final float maxX;
    private final float maxY;

    private final CollisionGrid collisionGrid;

    private final List<Location> locations = new ArrayList<>();
    private final List<Vector2f[]> vectorGrids = new ArrayList<>();

    /**
     * Constructor for creating a navigation grid.
     *
     * @param collisionGrid Grid of wall collision information
     * @param minX          Minimum X boundary for navigation grid
     * @param minY          Minimum Y boundary for navigation grid
     * @param maxX          Maximum X boundary for navigation grid
     * @param maxY          Maximum Y boundary for navigation grid
     */
    public NavigationGrid(CollisionGrid collisionGrid, float minX, float minY, float maxX, float maxY) {
        this.collisionGrid = collisionGrid;
        this.minX = Math.min(collisionGrid.getMinX(), minX);
        this.minY = Math.min(collisionGrid.getMinY(), minY);
        this.maxX = Math.max(collisionGrid.getMaxX(), maxX);
        this.maxY = Math.max(collisionGrid.getMaxY(), maxY);
        width = (int)(this.maxX - this.minX);
        height = (int)(this.maxY - this.minY);
    }

    /**
     * Gets the number of locations there are grids for.
     *
     * @return Number of navigation grid locations
     */
    public int getLocationCount() {
        return vectorGrids.size();
    }

    /**
     * Gets a location by its index value
     *
     * @param locationIndex Location's index value
     * @return Location at this index
     */
    public Location getLocation(int locationIndex) {
        return locations.get(locationIndex);
    }

    /**
     * Adds a location to the navigation grid so navigation data can be generated for it.
     *
     * @param location Location to add to the navigation grid and generate data for
     */
    public void addLocation(Location location) {
        locations.add(location);
        location.setNavGridId(locations.size()-1);
    }

    /**
     * Generates the navigation data for each location
     */
    public synchronized void generateLocationGrids() {
        List<Job> jobs = new ArrayList<>();
        for (Location location: locations) {
            Vector2f[] grid = new Vector2f[width*height];
            vectorGrids.add(grid);
            jobs.add(new Job(xIndex(location.getX()), yIndex(location.getY()), grid));
        }

        jobs.parallelStream().forEach((Job job) -> generateNavigationGrid(job.x, job.y, job.grid));
    }

    /**
     * Generates navigation grid data for a given location.
     *
     * @param startX X position of location
     * @param startY Y position of location
     * @param grid   Grid to store path results in
     */
    private void generateNavigationGrid(int startX, int startY, Vector2f[] grid) {
        Node node;

        Comparator<Node> comparator = new NodeComparator();
        PriorityQueue<Node> frontier = new PriorityQueue<>(100, comparator);

        Vector2f vec;

        node = new Node(startX, startY, startX, startY, 0f);
        frontier.add(node);

        while (!frontier.isEmpty()) {
            node = frontier.remove();

            if (grid[node.y*width+node.x] == null) {
                vec = new Vector2f(node.fromX - node.x, node.fromY - node.y);
                if (vec.x != 0 && vec.y != 0)
                    vec.normalize();
                grid[node.y * width + node.x] = vec;

                Integer[] dirs = {0, 1, 2, 3, 4, 5, 6, 7};
                ArrayUtils.shuffle(dirs);

                int wallsHit;
                int nx, ny;
                for (Integer dir : dirs) {
                    nx = node.x + dx[dir];
                    ny = node.y + dy[dir];

                    if (nx >= 0 && nx < width && ny >= 0 && ny < height && grid[ny * width + nx] == null) {
                        wallsHit = collisionGrid.hitWall(
                                minX + node.x,
                                minY + node.y,
                                minX + nx,
                                minY + ny);
                        if (wallsHit == 0) {
                            frontier.add(new Node(nx, ny, node.x, node.y, node.distance + (float)Math.hypot(nx - node.x, ny - node.y)));
                        }
                    }
                }
            }
        }

        for (int i=0; i<grid.length; i++) {
            if (grid[i] == null) {
                grid[i] = new Vector2f();
            }
        }
    }

    /**
     * Gets a vector for a location's navigation information at a given position.
     *
     * @param locationIndex Location's index number
     * @param x             World X position
     * @param y             World Y position
     * @return Vector at a world position for a location
     */
    public Vector2f getVector(int locationIndex, float x, float y) {
        if (isInBounds(x, y)) {
            return vectorGrids.get(locationIndex)[calcIndex(x, y)];
        }
        else {
            return new Vector2f(-x, -y).normalize();
        }
    }

    /**
     * Converts a world position into a grid index.
     *
     * @param x World X position
     * @param y World Y position
     * @return Grid index
     */
    private int calcIndex(float x, float y) {
        return (int)(Math.floor(y-minY) * width + Math.floor(x-minX));
    }

    /**
     * Converts a world's X position to an X index into 2D grid.
     *
     * @param x World X position
     * @return X index
     */
    private int xIndex(float x) {
        return (int)(Math.floor(x-minX));
    }

    /**
     * Converts a world's Y position to a Y index into 2D grid.
     *
     * @param y World Y position
     * @return Y index
     */
    private int yIndex(float y) {
        return (int)(Math.floor(y-minY));
    }

    /**
     * Gets the width of the grid.
     *
     * @return Width of the grid
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the grid.
     *
     * @return Height of the grid
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the minimum X boundary position.
     *
     * @return Minimum X position
     */
    public float getMinX() {
        return minX;
    }

    /**
     * Gets the minimum Y boundary position.
     *
     * @return Minimum Y position
     */
    public float getMinY() {
        return minY;
    }

    /**
     * Gets the maximum X boundary position.
     *
     * @return Maximum X position
     */
    public float getMaxX() {
        return maxX;
    }

    /**
     * Gets the maximum Y boundary position.
     *
     * @return Maximum Y position
     */
    public float getMaxY() {
        return maxY;
    }

    /**
     * Checks if a world position is within bounds of the navigation grid.
     *
     * @param x World X position
     * @param y World Y position
     * @return True if within bounds of the navigation grid
     */
    private boolean isInBounds(float x, float y) {
        return x >= minX && x < maxX && y >= minY && y < maxY;
    }


}
