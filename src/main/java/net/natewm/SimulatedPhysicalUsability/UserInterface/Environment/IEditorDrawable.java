package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;

import java.awt.*;

/**
 * Created by Nathan on 2/21/2017.
 */
public interface IEditorDrawable {
    /**
     * Draws this component.
     *
     * @param graphics2D Graphics to draw onto.
     * @param offsetX X offset of view.
     * @param offsetY Y offset of view.
     */
    void draw(Graphics2D graphics2D, int offsetX, int offsetY, int scale);


    /**
     * Gets the bounding rectangle for this object.
     *
     * @return Bounding rectangle.
     */
    Rect getRect();
}
