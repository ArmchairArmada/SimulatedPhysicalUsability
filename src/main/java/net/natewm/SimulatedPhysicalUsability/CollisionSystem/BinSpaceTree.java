package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;


/**
 * A spacial partitioning system where a rectangular area is recursively subdivided with alternating vertical and
 * horizontal cuts to form a search tree.  This is useful for performing a spacial query to find everything that
 * overlaps a test rectangle.
 *
 * @param <T> The type of data that will be stored in the the tree.
 */
public class BinSpaceTree<T> implements ICollisionCollection<T>{
    private final Node root;      // Root node
    private final int maxDepth;   // Maximum search depth for the tree.

    /**
     * Constructor for binary space search tree.
     *
     * @param x        The horizontal position of space's bounds.
     * @param y        The vertical position of space's bounds.
     * @param width    The width of the space's bounds.
     * @param height   The height of the space's bounds.
     * @param maxDepth The maximum depth permitted for search tree.
     */
    public BinSpaceTree(float x, float y, float width, float height, int maxDepth) {
        this.maxDepth = maxDepth;
        root = new Node(null, x, y, width, height);
    }

    /**
     * Inserts an object with an associated bounding rectangle into the tree.
     *
     * @param rect   Bounding rectangle of object to insert.
     * @param object Object to insert.
     */
    public void insert(Rect rect, T object) {
        root.insert(rect, object, maxDepth);
    }

    /**
     * Removes an object with an associated bounding rectangle from the tree.
     *
     * @param rect Rectangle of object to remove.
     * @return Object that has been removed.
     */
    public T remove(Rect rect) {
        return root.remove(rect, maxDepth);
    }

    /**
     * Finds all objects overlapping a given test rectangle and puts results into a given collection.
     *
     * @param rect          Test rectangle to find rectangles that overlap it.
     * @param outCollection Collection to add search results to.
     */
    public void findOverlapping(Rect rect, List<Pair<Rect, T>> outCollection) {
        root.findOverlapping(rect, outCollection, true);
    }

    /**
     * A node in the search tree.
     */
    private class Node {
        final Rect rect;                                       // Bounding rectangle
        final Node parent;                                     // Node's parent node
        final List<Pair<Rect, T>> objects = new LinkedList<>();// Object stored in this node
        Node childA = null;                              // Left or top child node
        Node childB = null;                              // Right or bottom child node


        /**
         * Node constructor.
         *
         * @param parent The Node's parent node.
         * @param x      The left edge of the node's rectangular area.
         * @param y      The top edge of the node's rectangular area.
         * @param width  The width of the node's rectangular area.
         * @param height The height of the node's rectangular area.
         */
        Node(Node parent, float x, float y, float width, float height) {
            this.parent = parent;
            rect = new Rect(x, y, width, height);
        }


        /**
         * Splits the node vertically, creating two child nodes side-by-side.
         */
        void splitVertical() {
            float half = rect.width / 2.0f; // Each child is half as wide
            float midX = rect.x + half;     // The horizontal position of the middle line.
            childA = new Node(this, rect.x, rect.y, half, rect.height);
            childB = new Node(this, midX, rect.y, half, rect.height);
        }


        /**
         * Splits the node horizontally, creating two child nodes with one on top of the other.
         */
        void splitHorizontal() {
            float half = rect.height / 2.0f;// Each child is half as tall.
            float midY = rect.y + half;     // The vertical position of the middle line.
            childA = new Node(this, rect.x, rect.y, rect.width, half);
            childB = new Node(this, rect.x, midY, rect.width, half);
        }


        /**
         * Recursively finds a node that can fully contain a given test rectangle.
         *
         * @param rect           A rectangle to search the tree with.
         * @param isVertical     Tree nodes alternate split either vertically or horizontally.
         * @param remainingDepth How much deeper we are allowed to recurse into the tree.
         * @return The node that fully contains the given rectangle.
         */
        Node findNode(Rect rect, boolean isVertical, int remainingDepth) {
            // Check if maximum recursion depth has been reached.  If it has, simply use this node.
            if (remainingDepth == 0)
                return this;

            // Check if this node needs to be split to have children.
            if (childA == null) {
                if (isVertical) {
                    splitVertical();
                }
                else {
                    splitHorizontal();
                }
            }

            // Check if one of the children can contain this rectangle.
            if (childA.rect.contains(rect)) {
                return childA.findNode(rect, !isVertical, remainingDepth-1);
            }
            else if (childB.rect.contains(rect)) {
                return childB.findNode(rect, !isVertical, remainingDepth-1);
            }

            // Children could not contain rect (overlaps both) so this node contains rect.
            return this;
        }


        /**
         * Recursively counts the number of nodes below this node.
         *
         * @return The number of nodes in this subtree.
         */
        int count() {
            int c = objects.size();
            if (childA != null) {
                c += childA.count();
            }
            if (childB != null) {
                c += childB.count();
            }
            return c;
        }


        /**
         * Insert an object into the tree with a rect key.
         *
         * @param rect     Rectangle bounding the object to insert.
         * @param object   Object to insert into tree.
         * @param maxDepth Maximum depth of the tree.
         */
        void insert(Rect rect, T object, int maxDepth) {
            Node node = findNode(rect, true, maxDepth);
            node.objects.add(new Pair<>(rect, object));
        }


        /**
         * Removes an object from the tree with the associated rectangle key.
         *
         * @param rect     Rectangle of object to find.
         * @param maxDepth Maximum depth of the tree.
         * @return Object that has just been removed.
         */
        @SuppressWarnings("unchecked")
        T remove(Rect rect, int maxDepth) {
            Node node = findNode(rect, true, maxDepth);
            Object object;
            for (Pair<Rect, T> pair : node.objects) {
                if (pair.getKey() == rect) {
                    object = pair.getValue();
                    node.objects.remove(pair);


                    if (node.objects.isEmpty() && node.parent != null)  {
                        node.parent.merge();
                    }


                    return (T)object;
                }
            }
            return null;
        }


        /**
         * If child nodes are empty, they can be merged into parent (to reduce search depth).
         */
        void merge() {
            if (childA.count() == 0 && childB.count() == 0) {
                // Since the children nodes are empty, we don't need them anymore.
                childA = null;
                childB = null;
                if (parent != null) {
                    parent.merge();
                }
            }
        }


        /**
         * Queries the tree for rectangles overlapping a test rectangle.
         *
         * @param rect           Query rectangle to use to test overlap.
         * @param outCollection  Collection to add overlapped rectangles and associated objects to.
         * @param isVertical     Recursive search alternates vertical and horizontal child nodes.
         */
        void findOverlapping(Rect rect, List<Pair<Rect, T>> outCollection, boolean isVertical) {
            // Check for overlaps with objects in this node and add to collection.
            for (Pair<Rect, T> pair : objects) {
                if (rect.isOverlapping(pair.getKey())) {
                    outCollection.add(pair);
                }
            }

            // Recurse into children.
            if (childA != null && childA.rect.isOverlapping(rect)) {
                childA.findOverlapping(rect, outCollection, !isVertical);
            }

            if (childB != null && childB.rect.isOverlapping(rect)) {
                childB.findOverlapping(rect, outCollection, !isVertical);
            }
        }
    }
}
