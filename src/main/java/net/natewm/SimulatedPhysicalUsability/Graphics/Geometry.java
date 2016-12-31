package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.common.nio.Buffers;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 12/27/2016.
 */
public class Geometry {
    List<Vector3f> vertices = new ArrayList<Vector3f>();
    List<Vector3f> normals = new ArrayList<Vector3f>();
    List<Vector3f> colors = new ArrayList<Vector3f>();
    List<Vector2f> uv = new ArrayList<Vector2f>();
    List<Triangle> triangles = new ArrayList<Triangle>();

    public Geometry() {
    }

    public void addVertex(Vector3f vertex) {
        vertices.add(vertex);
    }

    public void addNormal(Vector3f normal) {
        normals.add(normal);
    }

    public void addColor(Vector3f color) {
        colors.add(color);
    }

    public void addUv(Vector2f coordinate) {
        uv.add(coordinate);
    }

    public void addTriangle(Triangle triangle) {
        triangles.add(triangle);
    }

    public Vector3f getVertex(int index) {return vertices.get(index);}

    public Vector3f getNormal(int index) {return normals.get(index);}

    public Vector3f getColor(int index) {return colors.get(index);}

    public Vector2f getUv(int index) {return uv.get(index);}

    public Triangle getTriangle(int index) {return triangles.get(index);}

    public int vertexCount() {
        return vertices.size();
    }

    public int normalsCount() {
        return normals.size();
    }

    public int colorsCount() {
        return colors.size();
    }

    public int uvCount() {
        return uv.size();
    }

    public int trianglesCount() {
        return triangles.size();
    }

    public boolean checkConsistancy() {
        return (normals.size() == vertices.size() || normals.size() == 0)
            && (colors.size()  == vertices.size() || colors.size() == 0)
            && (uv.size()      == vertices.size() || uv.size() == 0);
    }

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

    public FloatBuffer makeUvBuffer() {
        FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(uv.size() * 2);
        for (Vector2f vector: uv) {
            floatBuffer.put(vector.x);
            floatBuffer.put(vector.y);
        }
        floatBuffer.flip();
        return floatBuffer;
    }

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
