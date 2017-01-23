package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

/**
 * Created by Nathan on 1/21/2017.
 */
public class Rectangle implements IShape {
    Rect rect;

    public Rectangle(float x, float y, float width, float height) {
        rect = new Rect(x, y, width, height);
    }

    @Override
    public boolean isOverlapping(IShape shape) {
        if (shape instanceof Rectangle) {
            return rect.isOverlapping(shape.getBoundingRect());
        }
        else if (shape instanceof Circle) {
            // TODO: Implement real circle/rectangle intersection testing.
        }

        return false;
    }

    @Override
    public boolean isInside(float x, float y) {
        return rect.isInside(x, y);
    }

    @Override
    public Rect getBoundingRect() {
        return rect;
    }

    @Override
    public void move(float x, float y) {

    }
}
