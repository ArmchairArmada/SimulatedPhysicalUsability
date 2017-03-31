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
 * Created by Nathan on 1/23/2017.
 */
public class NavigationGrid {
    private class Node {
        int x;
        int y;
        int fromX;
        int fromY;
        float distance;

        public Node(int x, int y, int fromX, int fromY, float distance) {
            this.x = x;
            this.y = y;
            this.fromX = fromX;
            this.fromY = fromY;
            this.distance = distance;
        }
    }

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

    static final int dx[] = {0, 0, -1, 1, -1, -1, 1, 1};
    static final int dy[] = {-1, 1, 0, 0, -1, 1, -1, 1};

    int width;
    int height;
    float minX;
    float minY;
    float maxX;
    float maxY;

    private CollisionGrid collisionGrid;

    //private List<Vector2f> locations = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();
    private List<Vector2f[]> vectorGrids = new ArrayList<>();

    public NavigationGrid(CollisionGrid collisionGrid) {
        // TODO: Also use Locations when they are finally available.
        this.collisionGrid = collisionGrid;
        width = collisionGrid.getWidth();
        height = collisionGrid.getHeight();
        minX = collisionGrid.getMinX();
        minY = collisionGrid.getMinY();
        maxX = collisionGrid.getMaxX();
        maxY = collisionGrid.getMaxY();
    }

    public int getLocationCount() {
        return vectorGrids.size();
    }

    /*
    public Vector2f getLocation(int locationIndex) {
        return locations.get(locationIndex);
    }
    */

    public Location getLocation(int locationIndex) {
        return locations.get(locationIndex);
    }

    /*
    public void addLocation(float x, float y) {
        locations.add(new Vector2f(x,y));
        generateNavigationGrid(xIndex(x), yIndex(y));
    }
    */

    public void addLocation(Location location) {
        locations.add(location);
        //generateNavigationGrid(xIndex(location.getX()), yIndex(location.getY()));
        location.setNavGridId(locations.size()-1);
    }

    public synchronized void generateLocationGrids() {
        /*
        locations.parallelStream().forEach((Location location) -> {
            generateNavigationGrid(xIndex(location.getX()), yIndex(location.getY()));
        });
        */

        for (Location location : locations) {
            generateNavigationGrid(xIndex(location.getX()), yIndex(location.getY()));
        }
    }

    private void generateNavigationGrid(int startX, int startY) {
        Node node;

        Comparator<Node> comparator = new NodeComparator();
        PriorityQueue<Node> frontier = new PriorityQueue<>(100, comparator);

        Vector2f[] grid = new Vector2f[width*height];
        Vector2f vec;

        node = new Node(startX, startY, startX, startY, 0f);
        frontier.add(node);

        while (!frontier.isEmpty()) {
            //System.out.println(frontier.size());

            node = frontier.remove();

            if (grid[node.y*width+node.x] == null) {
                //System.out.println(node.x + ", " + node.y + ", " + node.fromX + ", " + node.fromY + ", " + node.distance);
                //System.out.println(width + ", " + height);

                //grid[node.y*width+node.x] = new Vector2f(node.x-node.fromX, node.y-node.fromY);
                //grid[node.y * width + node.x] = new Vector2f(node.fromX - node.x, node.fromY - node.y).normalize();
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

                    /*
                    if (dir == 0 && node.y > 0 && !collisionGrid.hasTopWall(node.x, node.y)) {
                        if (node.y - 1 >= 0 && grid[(node.y - 1) * width + node.x] == null) {
                            frontier.add(new Node(node.x, node.y - 1, node.x, node.y, node.distance + 1f));
                        }
                    }

                    if (dir == 1 && node.y < height - 1 && !collisionGrid.hasTopWall(node.x, node.y + 1)) {
                        if (node.y + 1 >= 0 && grid[(node.y + 1) * width + node.x] == null) {
                            frontier.add(new Node(node.x, node.y + 1, node.x, node.y, node.distance + 1f));
                        }
                    }

                    if (dir == 2 && node.x > 0 && !collisionGrid.hasLeftWall(node.x, node.y)) {
                        if (node.x - 1 >= 0 && grid[node.y * width + node.x - 1] == null) {
                            frontier.add(new Node(node.x - 1, node.y, node.x, node.y, node.distance + 1f));
                        }
                    }

                    if (dir == 3 && node.x < width - 1 && !collisionGrid.hasLeftWall(node.x + 1, node.y)) {
                        if (node.x + 1 >= 0 && grid[node.y * width + node.x + 1] == null) {
                            frontier.add(new Node(node.x + 1, node.y, node.x, node.y, node.distance + 1f));
                        }
                    }
                    */
                }


                /*
                if (node.y > 0 && !collisionGrid.hasTopWall(node.x, node.y)) {
                    if (node.y - 1 >= 0 && grid[(node.y - 1) * width + node.x] == null) {
                        frontier.add(new Node(node.x, node.y - 1, node.x, node.y, node.distance + 1f));
                    }
                }

                if (node.y < height - 1 && !collisionGrid.hasTopWall(node.x, node.y + 1)) {
                    if (node.y + 1 >= 0 && grid[(node.y + 1) * width + node.x] == null) {
                        frontier.add(new Node(node.x, node.y + 1, node.x, node.y, node.distance + 1f));
                    }
                }

                if (node.x > 0 && !collisionGrid.hasLeftWall(node.x, node.y)) {
                    if (node.x - 1 >= 0 && grid[node.y * width + node.x - 1] == null) {
                        frontier.add(new Node(node.x - 1, node.y, node.x, node.y, node.distance + 1f));
                    }
                }

                if (node.x < width - 1 && !collisionGrid.hasLeftWall(node.x + 1, node.y)) {
                    if (node.x + 1 >= 0 && grid[node.y * width + node.x + 1] == null) {
                        frontier.add(new Node(node.x + 1, node.y, node.x, node.y, node.distance + 1f));
                    }
                }
                */
            }
        }

        for (int i=0; i<grid.length; i++) {
            if (grid[i] == null) {
                grid[i] = new Vector2f();
            }
        }

        vectorGrids.add(grid);
        //addGrid(grid);
    }

    private synchronized void addGrid(Vector2f[] grid) {
        vectorGrids.add(grid);
    }

    public Vector2f getVector(int locationIndex, float x, float y) {
        if (isInBounds(x, y)) {
            return vectorGrids.get(locationIndex)[calcIndex(x, y)];
        }
        else {
            return new Vector2f(-x, -y).normalize();
        }
    }

    private int calcIndex(float x, float y) {
        return (int)(Math.floor(y-minY) * width + Math.floor(x-minX));
    }

    private int xIndex(float x) {
        return (int)(Math.floor(x-minX));
    }

    private int yIndex(float y) {
        return (int)(Math.floor(y-minY));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public boolean isInBounds(float x, float y) {
        return x >= minX && x < maxX && y >= minY && y < maxY;
    }


}
