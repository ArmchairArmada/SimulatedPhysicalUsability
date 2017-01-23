package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

/**
 * Created by Nathan on 1/21/2017.
 */
public interface IShape {
    public Rect getBoundingRect();
    public void move(float x, float y);
    public boolean isOverlapping(IShape shape);
    public boolean isInside(float x, float y);
}
