package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

// TODO: See if this is needed -- not currently used?

/**
 * Created by Nathan on 1/21/2017.
 */
public class Circle implements IShape {
    float x;
    float y;
    float radius;
    Rect rect;

    public Circle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        rect = new Rect(x - radius, y - radius, 2.0f * radius, 2.0f * radius);
    }

    private void updateRect() {
        rect.x = x - radius;
        rect.y = y - radius;
        rect.width = 2.0f * radius;
        rect.height = 2.0f * radius;
    }

    public void move(float x, float y) {
        this.x = x;
        this.y = y;
        updateRect();
    }

    @Override
    public boolean isOverlapping(IShape shape) {
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            float differenceX = circle.x - x;
            float differenceY = circle.y - y;
            float sumRadius = radius + circle.radius;
            float distanceSquared = differenceX*differenceX + differenceY*differenceY;
            return distanceSquared <= (sumRadius * sumRadius);
        }
        else if (shape instanceof Rectangle) {
            // TODO: Handle circle/rectangle intersection

        }
        return false;
    }

    @Override
    public boolean isInside(float x, float y) {
        float differenceX = this.x - x;
        float differenceY = this.y - y;
        return (differenceX*differenceX + differenceY*differenceY) <= (radius * radius);
    }

    public void resize(float radius) {
        this.radius = radius;
        updateRect();
    }

    @Override
    public Rect getBoundingRect() {
        return rect;
    }
}
