package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

import javafx.util.Pair;

import java.util.List;

/**
 * Created by Nathan on 1/29/2017.
 */
public interface ICollisionCollection<T> {
    public void insert(Rect rect, T object);

    public T remove(Rect rect);

    public void findOverlapping(Rect rect, List<Pair<Rect, T>> outCollection);
}
