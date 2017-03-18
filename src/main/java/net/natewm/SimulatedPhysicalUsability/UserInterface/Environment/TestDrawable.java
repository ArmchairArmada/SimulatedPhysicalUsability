package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;
import net.natewm.SimulatedPhysicalUsability.Environment.Environment;

import java.awt.*;

/**
 * Created by Nathan on 2/21/2017.
 */
public class TestDrawable implements IEditorDrawable {
    Rect rect;
    Color color;

    public TestDrawable(int x, int y) {
        color = Color.getHSBColor((float)Math.random(), (float)Math.random(), (float)Math.random());
        rect = new Rect(x, y, 1, 1);
    }

    @Override
    public void draw(Graphics2D graphics2D, int offsetX, int offsetY, int scale) {
        graphics2D.setColor(color);
        graphics2D.fillOval((int)(rect.getX() * scale + offsetX+2), (int)(rect.getY() * scale + offsetY+2), scale-4, scale-4);
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public void applyToEnvironment(Environment environment) {

    }
}
