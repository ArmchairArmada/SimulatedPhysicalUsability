package net.natewm.SimulatedPhysicalUsability.Resources;

import com.jogamp.common.nio.Buffers;
import net.natewm.SimulatedPhysicalUsability.Rendering.Triangle;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 12/27/2016.
 */

/**
 * Stores information on vertices, normals, colors, uv coordinates and triangles.  From this data, OpenGL compatible
 * buffers can be generated.
 */
public class Geometry {
    List<Vector3f> vertices = new ArrayList<Vector3f>();
    List<Vector3f> normals = new ArrayList<Vector3f>();
    List<Vector3f> colors = new ArrayList<Vector3f>();
    List<Vector2f> uv = new ArrayList<Vector2f>();
    List<Triangle> triangles = new ArrayList<Triangle>();

    /**
     * General constructor.
     */
    public Geometry() {
    }

    /**
     * Adds a vertex to the geometry.
     *
     * @param vertex Vertex to add.
     */
    public void addVertex(Vector3f vertex) {
        vertices.add(vertex);
    }

    /**
     * Adds a vertex normal vector to the geometry.
     *
     * @param normal Normal to add.
     */
    public void addNormal(Vector3f normal) {
        normals.add(normal);
    }

    /**
     * Adds a vertex color to the geometry.
     *
     * @param color Color to add.
     */
    public void addColor(Vector3f color) {
        colors.add(color);
    }

    /**
     * Adds a vertex UV coordinate to the geometry.
     *
     * @param coordinate UV coordinate to add.
     */
    public void addUv(Vector2f coordinate) {
        uv.add(coordinate);
    }

    /**
     * Adds a triangle to the geometry, which are three indices into the other arrays.
     *
     * @param triangle Triangle to add.
     */
    public void addTriangle(Triangle triangle) {
        triangles.add(triangle);
    }

    /**
     * Gets a vertex at a given index;
     *
     * @param index Index of the vertex to get.
     * @return The vertex at the given index.
     */
    public Vector3f getVertex(int index) {return vertices.get(index);}

    /**
     * Gets a normal at a given index.
     *
     * @param index Index of the normal to get.
     * @return The normal at the given index.
     */
    public Vector3f getNormal(int index) {return normals.get(index);}

    /**
     * Gets a color at a given index.
     *
     * @param index Index of the color to get.
     * @return The color at the given index.
     */
    public Vector3f getColor(int index) {return colors.get(index);}

    /**
     * Gets a UV coordinate at the given index.
     *
     * @param index Index of the UV coordinate to get.
     * @return The UV coordinate at the given index.
     */
    public Vector2f getUv(int index) {return uv.get(index);}

    /**
     * Gets a triangle at a given index.
     *
     * @param index Index of the triangle to get.
     * @return The triangle at the given index.
     */
    public Triangle getTriangle(int index) {return triangles.get(index);}

    /**
     * Gets the number of vertices in the geometry.
     *
     * @return Vertex count.
     */
    public int vertexCount() {
        return vertices.size();
    }

    /**
     * Gets the number of normals in the geometry.  This should be either the same as vertex count or zero (if there are
     * no vertex normals.)
     *
     * @return Normal count.
     */
    public int normalsCount() {
        return normals.size();
    }

    /**
     * Gets the number of colors in the geometry.  This should be either the same as vertex count or zero (if there are
     * no vertex colors.)
     *
     * @return Color count
     */
    public int colorsCount() {
        return colors.size();
    }

    /**
     * Gets the number of UV coordinates in the geometry.  This should be either the same as the vertex count or zero
     * (if there are no vertex UV coordinates.)
     *
     * @return UV coordinate count.
     */
    public int uvCount() {
        return uv.size();
    }

    /**
     * Gets the number of triangles in the geometry.
     *
     * @return Triangle count.
     */
    public int trianglesCount() {
        return triangles.size();
    }

    public boolean checkConsistancy() {
        return (normals.size() == vertices.size() || normals.size() == 0)
            && (colors.size()  == vertices.size() || colors.size() == 0)
            && (uv.size()      == vertices.size() || uv.size() == 0);
    }

    /**
     * Creates an OpenGL compatible buffer of vertices.
     *
     * @return A buffer of vertices.
     */
    public FloatBuffer makeVertexBuffer() {
        FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(vertices.size() * 3);
        for (Vector3f vector: vertices) {
            floatBuffer.put(vector.x);
            floatBuffer.put(vector.y);
            floatBuffer.put(vector.z);
        }
        floatBuffer.flip();
        return floatBuffer;
    }

    /**
     * Creates an OpenGL compatible buffer of normals.
     *
     * @return A buffer of normals.
     */
    public FloatBuffer makeNormalBuffer() {
        FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(normals.size() * 3);
        for (Vector3f vector: normals) {
            floatBuffer.put(vector.x);
            floatBuffer.put(vector.y);
            floatBuffer.put(vector.z);
        }
        floatBuffer.flip();
        return floatBuffer;
    }

    /**
     * Creates an OpenGL compatible buffer of colors.
     *
     * @return A buffer of colors.
     */
    public FloatBuffer makeColorBuffer() {
        FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(colors.size() * 3);
        for (Vector3f vector: colors) {
            floatBuffer.put(vector.x);
            floatBuffer.put(vector.y);
            floatBuffer.put(vector.z);
        }
        floatBuffer.flip();
        return floatBuffer;
    }

    /**
     * Creates an OpenGL compatible buffer of UV coordinates.
     *
     * @return Buffer of UV coordinates.
     */
    public FloatBuffer makeUvBuffer() {
        FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(uv.size() * 2);
        for (Vector2f vector: uv) {
            floatBuffer.put(vector.x);
            floatBuffer.put(vector.y);
        }
        floatBuffer.flip();
        return floatBuffer;
    }

    /**
     * Creates an OpenGL compatible buffer of triangle indices.
     *
     * @return A buffer of triangle indicies.
     */
    public IntBuffer makeTriangleBuffer() {
        IntBuffer intBuffer = Buffers.newDirectIntBuffer(triangles.size() * 3);
        for (Triangle triangle: triangles) {
            intBuffer.put(triangle.a);
            intBuffer.put(triangle.b);
            intBuffer.put(triangle.c);
        }
        intBuffer.flip();
        return intBuffer;
    }

    /**
     * Computes the spherical radius of the geometry (maximum distance from origin to furthest vertex).
     *
     * @return The radius of the geometry.
     */
    public float getRadius() {
        float radius = 0f;
        float maxRadius = 0f;
        for (Vector3f vector : vertices) {
            radius = vector.length();
            if (radius > maxRadius)
                maxRadius = radius;
        }
        return radius;
    }
}
