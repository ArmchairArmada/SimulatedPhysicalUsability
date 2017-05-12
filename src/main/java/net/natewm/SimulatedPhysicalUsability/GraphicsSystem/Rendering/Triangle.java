package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

/**
 * Stores index values used for forming a triangle
 */
public class Triangle {
    public final int a;
    public final int b;
    public final int c;

    /**
     * Creates triangle using given vertex indexes
     *
     * @param a Vertex index 1
     * @param b Vertex index 2
     * @param c Vertex index 3
     */
    public Triangle(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
