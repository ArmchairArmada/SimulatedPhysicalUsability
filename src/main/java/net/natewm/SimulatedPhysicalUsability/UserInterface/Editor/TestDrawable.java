package net.natewm.SimulatedPhysicalUsability.UserInterface.Editor;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;

import java.awt.*;

/**
 * Created by Nathan on 2/21/2017.
 */
public class TestDrawable implements IEditorDrawable {
    Rect rect;

    public TestDrawable(int x, int y) {
        rect = new Rect(x+1, y+1, 23, 23);
    }

    @Override
    public void draw(Graphics2D graphics2D, int offsetX, int offsetY) {
        graphics2D.setColor(Color.blue);
        graphics2D.fillOval((int)(rect.getX() + offsetX), (int)(rect.getY() + offsetY), 23, 23);
    }

    @Override
    public Rect getRect() {
        return rect;
    }
}
