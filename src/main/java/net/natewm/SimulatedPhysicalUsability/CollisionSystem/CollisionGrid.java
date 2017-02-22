package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

import net.natewm.SimulatedPhysicalUsability.Environment.Walls;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid for checking collisions with walls.
 *
 * @param <T>
 */
public class CollisionGrid<T> {
    public static int HORIZONTAL = 1;
    public static int VERTICAL = 2;

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

    public boolean hasTopWall(int x, int y) {
        return ((Cell)cells[y * width + x]).topWall;
    }

    public boolean hasLeftWall(int x, int y) {
        return ((Cell)cells[y * width + x]).leftWall;
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

    public List<T> getSurroundingList(float x, float y) {
        List<T> output = new ArrayList<T>();
        int cx = (int)Math.floor(x - minX);
        int cy = (int)Math.floor(y - minY);
        int sx = Math.max(cx-1, 0);
        int sy = Math.max(cy-1, 0);
        int ex = Math.min(cx+2, width);
        int ey = Math.min(cy+2, height);

        for (int j=sy; j<ey; j++) {
            for (int i=sx; i<ex; i++) {
                output.addAll(((Cell)cells[j * width + i]).items);
            }
        }

        return output;
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

    public int hitWall(float startX, float startY, float endX, float endY) {
        float x = startX;
        float y = startY;
        float ox;
        float oy;
        float dxf = (endX - startX);
        float dyf = (endY - startY);
        int dx = (int)dxf;
        int dy = (int)dyf;
        float stepX;
        float stepY;
        int steps;
        //boolean horizontal;
        //boolean vertical;

        int wallsHit = 0;

        /*

        if (dxf == 0f && dyf == 0f) {
            return false;
        }
        else if (Math.abs(dxf) > Math.abs(dyf)) {
            // Changes more in X direction than in Y
            stepX = Math.copySign(1f, dxf);
            stepY = dy / Math.abs(dxf);
            steps = (int)(dxf / stepX);
        }
        else {
            stepX = dx / Math.abs(dyf);
            stepY = Math.copySign(1f, dyf);
            steps = (int)(dyf / stepY);
        }

        for (int i=0; i<steps; i++) {
            ox = x;
            oy = y;
            x += stepX;
            y += stepY;
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
        }

        ox = x;
        oy = y;
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

        */


        ox = startX;
        oy = startY;
        x = endX;
        y = endY;
        dx = (int)(Math.floor(x)) - (int)(Math.floor(ox));
        dy = (int)(Math.floor(y)) - (int)(Math.floor(oy));

        //horizontal = false;
        //vertical = false;

        //System.out.println(x + ", " + y);

        if (ox > minX && ox < maxX && oy > minY && oy < maxY
        && x > minX && x < maxX && y > minY && y < maxY) {
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
