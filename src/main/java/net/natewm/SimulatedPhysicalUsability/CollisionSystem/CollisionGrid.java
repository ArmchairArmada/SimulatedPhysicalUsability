package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

import net.natewm.SimulatedPhysicalUsability.Environment.Walls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 1/22/2017.
 */
public class CollisionGrid<T> {
    private class Cell {
        public boolean topWall = false;
        public boolean leftWall = false;
        public List<T> items = new ArrayList<>();
    }

    private final List<T> EMPTY_LIST = new ArrayList<>();

    int width;
    int height;
    float minX;
    float minY;
    float maxX;
    float maxY;
    Object[] cells;

    public CollisionGrid() {
    }

    public CollisionGrid(Walls walls) {
        reset(walls);
    }

    public void reset(Walls walls) {
        minX = walls.getMinX()-1;
        minY = walls.getMinY()-1;
        maxX = walls.getMaxX()+1;
        maxY = walls.getMaxY()+1;

        // TODO: Check if one should be added.
        width = (int)(Math.ceil(maxX) - Math.floor(minX));
        height = (int)(Math.ceil(maxY) - Math.floor(minY));
        cells = new Object[width * height];
        for (int i=0; i<width*height; i++) {
            cells[i] = new Cell();
        }

        int x;
        int y;
        boolean horizontal;
        for (Walls.Wall wall : walls.getWalls()) {
            x = (int)(Math.floor(Math.min(wall.startX, wall.endX)-minX));
            y = (int)(Math.floor(Math.min(wall.startY, wall.endY)-minY));

            //System.out.println(x + ", " + y);

            horizontal = Math.abs(wall.endX - wall.startX) > Math.abs(wall.endY - wall.startY);

            if (horizontal) {
                ((Cell)cells[y * width + x]).topWall = true;
            }
            else {
                ((Cell)cells[y * width + x]).leftWall = true;
            }
        }
    }

    private boolean isInBounds(float x, float y) {
        return x >= minX && x < maxX && y >= minY && y < maxY;
    }

    private int calcIndex(float x, float y) {
        return (int)(Math.floor(y-minY) * width + Math.floor(x-minX));
    }

    public void put(float x, float y, T object) {
        // We are just going to ignore it if it is out of bounds.
        if (isInBounds(x, y)) {
            int index = calcIndex(x, y);
            ((Cell) cells[index]).items.add(object);
        }
    }

    public void remove(float x, float y, T object) {
        if (isInBounds(x, y)) {
            int index = calcIndex(x, y);
            Cell cell = (Cell)cells[index];
            cell.items.remove(object);
        }
    }

    public List<T> getList(float x, float y) {
        // We are just going to ignore it if it is out of bounds.
        if (isInBounds(x, y)) {
            int index = calcIndex(x, y);
            return ((Cell) cells[index]).items;
        }
        return EMPTY_LIST;
    }

    public List<T> getList(Rect rect) {
        // TODO: Change to using callback function?
        List<T> output = new ArrayList<T>();
        int sx = (int)Math.floor(rect.getX() - minX);
        int sy = (int)Math.floor(rect.getY() - minY);
        int ex = (int)Math.floor(rect.getX() + rect.getWidth() - minX)+1;
        int ey = (int)Math.floor(rect.getY() + rect.getHeight() - minY)+1;

        if (sx < 0) sx = 0;
        if (ex >= width) ex = width;
        if (sy < 0) sy = 0;
        if (ey >= height) ey = height;

        for (int j=sy; j<ey; j++) {
            for (int i=sx; i<ex; i++) {
                output.addAll(((Cell)cells[j * width + i]).items);
            }
        }

        return output;
    }

    public boolean hitWall(float startX, float startY, float endX, float endY) {
        float x = startX;
        float y = startY;
        float ox;
        float oy;
        int dx = (int)(endX - startX);
        int dy = (int)(endY - startY);
        float stepX;
        float stepY;
        int steps;
        boolean horizontal;
        boolean vertical;


        ox = startX;
        oy = startY;
        x = endX;
        y = endY;
        dx = (int)(Math.floor(x)) - (int)(Math.floor(ox));
        dy = (int)(Math.floor(y)) - (int)(Math.floor(oy));

        horizontal = false;
        vertical = false;

        //System.out.println(x + ", " + y);

        if (ox > minX && ox < maxX && oy > minY && oy < maxY
        && x > minX && x < maxX && y > minY && y < maxY) {
            if (dx < 0) {
                vertical = ((Cell) cells[calcIndex(ox, oy)]).leftWall
                        | ((Cell) cells[calcIndex(ox, y)]).leftWall;
            } else if (dx > 0) {
                vertical = ((Cell) cells[calcIndex(x, oy)]).leftWall
                        | ((Cell) cells[calcIndex(x, y)]).leftWall;
            }

            if (dy < 0) {
                horizontal = ((Cell) cells[calcIndex(ox, oy)]).topWall
                        | ((Cell) cells[calcIndex(x, oy)]).topWall;
            } else if (dy > 0) {
                horizontal = ((Cell) cells[calcIndex(ox, y)]).topWall
                        | ((Cell) cells[calcIndex(x, y)]).topWall;
            }

            if (horizontal || vertical) {
                return true;
            }
        }


        /*
        if (Math.abs(dx) > Math.abs(dy)) {
            // Changes more in X direction than in Y
            stepX = Math.copySign(1f, dx);
            stepY = dy / Math.abs(dx);
            steps = (int)(dx / stepX);
        }
        else {
            stepX = dx / Math.abs(dy);
            stepY = Math.copySign(1f, dy);
            steps = (int)(dy / stepY);
        }

        for (int i=0; i<steps; i++) {
            ox = x;
            oy = y;
            x += stepX;
            y += stepY;
            dx = x - ox;
            dy = y - oy;

            horizontal = false;
            vertical = false;

            if (x >=0 && x < width && y >= 0 && y < height) {
                if (dx < 0) {
                    vertical = ((Cell) cells[calcIndex(ox, oy)]).leftWall
                            | ((Cell) cells[calcIndex(ox, y)]).leftWall;
                } else if (dx > 0) {
                    vertical = ((Cell) cells[calcIndex(x, oy)]).leftWall
                            | ((Cell) cells[calcIndex(x, y)]).leftWall;
                }

                if (dy < 0) {
                    horizontal = ((Cell) cells[calcIndex(ox, oy)]).topWall
                            | ((Cell) cells[calcIndex(x, oy)]).topWall;
                } else if (dy > 0) {
                    horizontal = ((Cell) cells[calcIndex(ox, y)]).topWall
                            | ((Cell) cells[calcIndex(x, y)]).topWall;
                }

                if (horizontal || vertical) {
                    return true;
                }
            }
        }
        */

        return false;
    }
}