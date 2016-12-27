package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by Nathan on 12/27/2016.
 */
public class Mesh {
    static final int FLOAT_SIZE = 4;
    static final int INT_SIZE = 4;

    IntBuffer vao;
    IntBuffer vbo;

    int triangleID = 0;
    int vertexId = 0;
    int normalId = 0;
    int colorId = 0;
    int uvId = 0;

    boolean hasNormals;
    boolean hasColors;
    boolean hasUvs;

    int indexCount;
    int triangleCount;
    int vertexCount;

    public Mesh(GL3 gl, Geometry geometry /*, Material material */) throws Exception {
        if (!geometry.checkConsistancy())
            throw new Exception("Inconsistant Geometry Buffer");

        hasNormals = geometry.normalsCount() > 0;
        hasColors = geometry.colorsCount() > 0;
        hasUvs = geometry.uvCount() > 0;

        indexCount = geometry.vertexCount() * 3;
        triangleCount = geometry.trianglesCount();
        vertexCount = geometry.vertexCount();

        genVAO(gl, geometry);
    }

    private int countEnabledBuffers() {
        int i = 2;  // Triangles and vertices;
        if (hasNormals) i++;
        if (hasColors) i++;
        if (hasUvs) i++;
        return i;
    }

    private void genVAO(GL3 gl, Geometry geometry /*, Material material */) {
        FloatBuffer vertexBuffer = geometry.makeVertexBuffer();
        FloatBuffer normalBuffer = geometry.makeNormalBuffer();
        FloatBuffer colorBuffer = geometry.makeColorBuffer();
        FloatBuffer uvBuffer = geometry.makeUvBuffer();
        IntBuffer triangleBuffer = geometry.makeTriangleBuffer();
        int nextIndex = 2;
        int enabledBuffers = countEnabledBuffers();

        vao = Buffers.newDirectIntBuffer(1);
        vbo = Buffers.newDirectIntBuffer(enabledBuffers);

        gl.glGenVertexArrays(1, vao);
        gl.glBindVertexArray(vao.get(0));

        gl.glGenBuffers(enabledBuffers, vbo);
        triangleID = vbo.get(0);
        vertexId = vbo.get(1);

        // TRIANGLES
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, triangleID);
        gl.glBufferData(gl.GL_ELEMENT_ARRAY_BUFFER, triangleBuffer.capacity() * INT_SIZE, triangleBuffer, gl.GL_STATIC_DRAW);

        // VERTEX POSITIONS
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vertexId);
        gl.glBufferData(gl.GL_ARRAY_BUFFER, vertexBuffer.capacity() * FLOAT_SIZE, vertexBuffer, gl.GL_STATIC_DRAW);
        // TODO: Get attribute numbers from vertex shader
        gl.glVertexAttribPointer(0, 3, gl.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        // NORMALS
        if (hasNormals) {
            normalId = vbo.get(nextIndex);
            nextIndex++;
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, normalId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, normalBuffer.capacity() * FLOAT_SIZE, normalBuffer, gl.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(1, 3, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(1);
        }

        // COLORS
        if (hasColors) {
            colorId = vbo.get(nextIndex);
            nextIndex++;
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, colorId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, colorBuffer.capacity() * FLOAT_SIZE, colorBuffer, gl.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(2, 3, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(2);
        }

        // UV
        if (hasUvs) {
            uvId = vbo.get(nextIndex);
            nextIndex++;
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, uvId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, uvBuffer.capacity() * FLOAT_SIZE, uvBuffer, gl.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(3, 2, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(3);
        }
    }

    public void dispose(GL3 gl) {
        gl.glBindVertexArray(vbo.get(0));
        gl.glDisableVertexAttribArray(0);
        gl.glBindVertexArray(0);

        gl.glDeleteBuffers(vbo.capacity(), vbo);
        gl.glDeleteVertexArrays(vao.capacity(), vao);
    }

    public void bind(GL3 gl) {
        gl.glBindVertexArray(vao.get(0));
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, triangleID);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vertexId);
    }

    public void render(GL3 gl) {
        gl.glDrawElements(gl.GL_TRIANGLES, indexCount, gl.GL_UNSIGNED_INT, 0);
    }

    public void unbind(GL3 gl) {
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(0);
    }

    public void easyRender(GL3 gl) {
        bind(gl);
        render(gl);
        unbind(gl);
    }
}
