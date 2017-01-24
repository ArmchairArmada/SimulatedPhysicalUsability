package net.natewm.SimulatedPhysicalUsability.Navigation;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
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


    int width;
    int height;
    float minX;
    float minY;
    float maxX;
    float maxY;

    public CollisionGrid collisionGrid;

    public List<Vector2f[]> vectorGrids = new ArrayList<>();

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

    public void addLocation(float x, float y) {
        generateNavigationGrid(xIndex(x), yIndex(y));
    }

    private void generateNavigationGrid(int startX, int startY) {
        Node node;

        Comparator<Node> comparator = new NodeComparator();
        PriorityQueue<Node> frontier = new PriorityQueue<>(100, comparator);

        Vector2f[] grid = new Vector2f[width*height];

        Node[] nodes = new Node[width*height];
        node = new Node(startX, startY, startX, startY, 0f);
        nodes[startY*width+startX] = node;
        frontier.add(node);

        while (!frontier.isEmpty()) {
            //System.out.println(frontier.size());

            node = frontier.remove();

            //System.out.println(node.x + ", " + node.y + ", " + node.fromX + ", " + node.fromY + ", " + node.distance);
            //System.out.println(width + ", " + height);

            //grid[node.y*width+node.x] = new Vector2f(node.x-node.fromX, node.y-node.fromY);
            grid[node.y*width+node.x] = new Vector2f(node.fromX-node.x, node.fromY-node.y);

            if (node.y > 0 && !collisionGrid.hasTopWall(node.x, node.y)) {
                if (node.y-1 >= 0 && grid[(node.y-1)*width+node.x] == null) {
                    frontier.add(new Node(node.x, node.y-1, node.x, node.y, node.distance+1f));
                }
            }

            if (node.y < height-1 && !collisionGrid.hasTopWall(node.x, node.y+1)) {
                if (node.y+1 >= 0 && grid[(node.y+1)*width+node.x] == null) {
                    frontier.add(new Node(node.x, node.y+1, node.x, node.y, node.distance+1f));
                }
            }

            if (node.x > 0 && !collisionGrid.hasLeftWall(node.x, node.y)) {
                if (node.x-1 >= 0 && grid[node.y*width+node.x-1] == null) {
                    frontier.add(new Node(node.x-1, node.y, node.x, node.y, node.distance+1f));
                }
            }

            if (node.x < width-1 && !collisionGrid.hasLeftWall(node.x+1, node.y)) {
                if (node.x+1 >= 0 && grid[node.y*width+node.x+1] == null) {
                    frontier.add(new Node(node.x+1, node.y, node.x, node.y, node.distance+1f));
                }
            }
        }

        vectorGrids.add(grid);
    }

    public Vector2f getVector(int locationIndex, float x, float y) {
        if (isInBounds(x, y)) {
            return vectorGrids.get(locationIndex)[calcIndex(x, y)];
        }
        else {
            Vector2f vector2f = new Vector2f(-x, -y).normalize();
            return vector2f;
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
