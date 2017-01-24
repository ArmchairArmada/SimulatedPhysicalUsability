package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.Utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 1/22/2017.
 */
public class MazeGenerator {
    private class Cell {
        public boolean visited = false;
        public boolean topWall = true;
        public boolean leftWall = true;
    }

    private class Dir {
        int dx;
        int dy;

        public Dir(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    int width;
    int height;
    private final float removeWallProbability;

    public MazeGenerator(int width, int height, float removeWallProbability) {
        this.width = width;
        this.height = height;
        this.removeWallProbability = removeWallProbability;
    }

    public Walls generate() {
        Walls walls = new Walls();
        Cell cell;
        int halfWidth = width/2;
        int halfHeight = height/2;

        Cell[] cells = new Cell[width * height];
        for (int i=0; i<cells.length; i++) {
            cells[i] = new Cell();
        }

        recursiveMaze(cells, halfHeight, halfWidth, 0, 0);

        for (int y=1; y<height; y++) {
            for (int x=1; x<width; x++) {
                if (Math.random() < removeWallProbability)
                    ((Cell) cells[y*width+x]).topWall = false;

                if (Math.random() < removeWallProbability)
                    ((Cell) cells[y*width+x]).leftWall = false;
            }
        }

        for (int y=0; y<height; y++) {
            for (int x = 0; x<width; x++) {
                cell = cells[y * width + x];

                if (cell.topWall) {
                    walls.addWall(new Walls.Wall(x-halfWidth, y-halfHeight, x-halfWidth+1, y-halfHeight));
                }

                if (cell.leftWall) {
                    walls.addWall(new Walls.Wall(x-halfWidth, y-halfHeight, x-halfWidth, y-halfHeight+1));
                }
            }
        }

        for (int x=0; x<width; x++) {
            walls.addWall(new Walls.Wall(x-halfWidth, halfHeight, x-halfWidth+1, halfHeight));
        }

        for (int y=0; y<height; y++) {
            walls.addWall(new Walls.Wall(halfWidth, y-halfHeight, halfWidth, y-halfHeight+1));
        }

        return walls;
    }

    private void recursiveMaze(Cell[] cells, int x, int y, int dx, int dy) {
        //System.out.println(x + ", " + y + ", " + dx + ", " + dy);

        if (x < 0 || x >= width || y < 0 || y >= height || cells[y*width+x].visited)
            return;

        Cell cell = cells[y*width+x];
        Cell oldCell = cells[(y-dy)*width+x-dx];

        cell.visited = true;

        if (dx == -1) {
            oldCell.leftWall = false;
        }
        else if (dx == 1) {
            cell.leftWall = false;
        }

        if (dy == -1) {
            oldCell.topWall = false;
        }
        else if (dy == 1) {
            cell.topWall = false;
        }

        Dir[] dirs = new Dir[]{
                new Dir(-1, 0),
                new Dir(1, 0),
                new Dir(0, -1),
                new Dir(0, 1),
        };

        ArrayUtils.shuffle(dirs);

        for(Dir dir : dirs) {
            recursiveMaze(cells, x+dir.dx, y+dir.dy, dir.dx, dir.dy);
        }
    }
}
