package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

import javafx.util.Pair;

import java.util.List;

/**
 * Interface for collection of collision rectangles.
 *
 * @param <T> Object type to store in this collection.
 */
public interface ICollisionCollection<T> {
    /**
     * Insert an object with the associated rectangle into collection.
     *
     * @param rect   Bounding rectangle of object to add.
     * @param object Object to add to collection.
     */
    void insert(Rect rect, T object);


    /**
     * Removes an object with a given rectangle from the collection.
     *
     * @param rect Rectangle associated with the object.
     * @return Object that has just been removed.
     */
    T remove(Rect rect);


    /**
     * Finds objects with bounding rectangles overlapping a given test rectangle.
     *
     * @param rect          Query rectangle to use to find overlaps.
     * @param outCollection Collection to add query search results to.
     */
    void findOverlapping(Rect rect, List<Pair<Rect, T>> outCollection);
}
