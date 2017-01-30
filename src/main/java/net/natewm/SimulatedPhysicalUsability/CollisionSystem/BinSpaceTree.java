package net.natewm.SimulatedPhysicalUsability.CollisionSystem;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nathan on 1/29/2017.
 */
public class BinSpaceTree<T> implements ICollisionCollection<T>{
    private class Node {
        public Rect rect;                                       // Bounding rectangle
        public Node parent;
        public Node childA = null;
        public Node childB = null;
        public List<Pair<Rect, T>> objects = new LinkedList<>(); // Object stored in this node

        public Node(Node parent, float x, float y, float width, float height) {
            this.parent = parent;
            rect = new Rect(x, y, width, height);
        }

        public void splitVertical() {
            float half = rect.width / 2.0f;
            float midX = rect.x + half;
            childA = new Node(this, rect.x, rect.y, half, rect.height);
            childB = new Node(this, midX, rect.y, half, rect.height);
        }

        public void splitHorizontal() {
            float half = rect.height / 2.0f;
            float midY = rect.y + half;
            childA = new Node(this, rect.x, rect.y, rect.width, half);
            childB = new Node(this, rect.x, midY, rect.width, half);
        }

        public Node findNode(Rect rect, boolean isVertical, int remainingDepth) {
            if (remainingDepth == 0)
                return this;

            if (childA == null) {
                if (isVertical) {
                    splitVertical();
                }
                else {
                    splitHorizontal();
                }
            }

            if (childA.rect.contains(rect)) {
                return childA.findNode(rect, !isVertical, remainingDepth-1);
            }
            else if (childB.rect.contains(rect)) {
                return childB.findNode(rect, !isVertical, remainingDepth-1);
            }

            return this;
        }

        public int count() {
            int c = objects.size();
            if (childA != null) {
                c += childA.count();
            }
            if (childB != null) {
                c += childB.count();
            }
            return c;
        }

        public void insert(Rect rect, T object, int maxDepth) {
            Node node = findNode(rect, true, maxDepth);
            node.objects.add(new Pair<Rect, T>(rect, object));
        }

        public T remove(Rect rect, int maxDepth) {
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

        public void merge() {
            if (childA.count() == 0 && childB.count() == 0) {
                // Since the children nodes are empty, we don't need them anymore.
                childA = null;
                childB = null;
                if (parent != null) {
                    parent.merge();
                }
            }
        }

        public void findOverlapping(Rect rect, List<Pair<Rect, T>> outCollection, boolean isVertical, int remainingDepth) {
            if (remainingDepth == 0)
                return;

            for (Pair<Rect, T> pair : objects) {
                if (rect.isOverlapping(pair.getKey())) {
                    outCollection.add(pair);
                }
            }

            if (childA != null) {
                childA.findOverlapping(rect, outCollection, !isVertical, remainingDepth-1);
            }

            if (childB != null) {
                childB.findOverlapping(rect, outCollection, !isVertical, remainingDepth-1);
            }
        }
    }

    private Node root;
    private int maxDepth;

    public BinSpaceTree(float x, float y, float width, float height, int maxDepth) {
        this.maxDepth = maxDepth;
        root = new Node(null, x, y, width, height);
    }

    public void insert(Rect rect, T object) {
        root.insert(rect, object, maxDepth);
    }

    public T remove(Rect rect) {
        return root.remove(rect, maxDepth);
    }

    public void findOverlapping(Rect rect, List<Pair<Rect, T>> outCollection) {
        root.findOverlapping(rect, outCollection, true, maxDepth);
    }
}
