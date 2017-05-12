package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;
import net.natewm.SimulatedPhysicalUsability.Environment.Walls;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;

import java.awt.*;

/**
 * Created by Nathan on 2/22/2017.
 */
public class WallDrawable implements IEditorDrawable {
    private static final float WALL_WIDTH = 0.2f;

    private final Rect rect;
    private final int x;
    private final int y;
    private final boolean horizontal;

    public WallDrawable(float x, float y, boolean horizontal) {
        this.x = (int)Math.floor(x);
        this.y = (int)Math.floor(y);
        this.horizontal = horizontal;

        if (horizontal)
            rect = new Rect((float)Math.floor(x+WALL_WIDTH) - WALL_WIDTH, (float)Math.floor(y+WALL_WIDTH) - 2f*WALL_WIDTH, 1f + 2f*WALL_WIDTH, 4f*WALL_WIDTH);
        else
            rect = new Rect((float)Math.floor(x+WALL_WIDTH) - 2f*WALL_WIDTH, (float)Math.floor(y+WALL_WIDTH) - WALL_WIDTH, 4f*WALL_WIDTH, 1f + 2f*WALL_WIDTH);
    }

    @Override
    public void draw(Graphics2D graphics2D, int offsetX, int offsetY, int scale) {
        graphics2D.setColor(Color.black);

        //graphics2D.fillRect((int)(rect.getX() * scale + offsetX), (int)(rect.getY() * scale + offsetY),
        //        (int)(rect.getWidth() * scale), (int)(rect.getHeight() * scale));

        if (horizontal) {
            graphics2D.fillRect((x*scale+offsetX)-2, (y*scale+offsetY)-2, scale+5, 5);
        }
        else {
            graphics2D.fillRect((x*scale+offsetX)-2, (y*scale+offsetY)-2, 5, scale+5);
        }
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public void apply(ProjectData projectData) {
        if (horizontal)
            projectData.getWalls().addWall(new Walls.Wall(x, y, x + 1, y));
        else
            projectData.getWalls().addWall(new Walls.Wall(x, y, x, y + 1));
    }
}
