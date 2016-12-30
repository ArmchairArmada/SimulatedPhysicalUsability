package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import com.sun.prism.impl.BufferUtil;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by Nathan on 12/27/2016.
 */
public class Mesh {
    IntBuffer vao;
    IntBuffer vbo;

    Material material;

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
    int elementCount;

    public Mesh(GL3 gl, Geometry geometry , Material material) throws Exception {
        if (!geometry.checkConsistancy())
            throw new Exception("Inconsistant Geometry Buffer");

        this.material = material;

        hasNormals = geometry.normalsCount() > 0;
        hasColors = geometry.colorsCount() > 0;
        hasUvs = geometry.uvCount() > 0;

        indexCount = geometry.vertexCount() * 3;
        triangleCount = geometry.trianglesCount();
        vertexCount = geometry.vertexCount();
        elementCount = triangleCount * 3;

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
        int attrib = 0;
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
        gl.glBufferData(gl.GL_ELEMENT_ARRAY_BUFFER, triangleBuffer.capacity() * Integer.BYTES, triangleBuffer, gl.GL_STATIC_DRAW);

        // VERTEX POSITIONS
        attrib = material.getAttributeLocation(gl, "position");
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vertexId);
        gl.glBufferData(gl.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, gl.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(attrib, 3, gl.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(attrib);

        // NORMALS
        if (hasNormals) {
            normalId = vbo.get(nextIndex);
            nextIndex++;
            attrib = material.getAttributeLocation(gl, "normal");
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, normalId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, normalBuffer.capacity() * Float.BYTES, normalBuffer, gl.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(attrib, 3, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attrib);
        }

        // COLORS
        if (hasColors) {
            colorId = vbo.get(nextIndex);
            nextIndex++;
            attrib = material.getAttributeLocation(gl, "color");
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, colorId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, colorBuffer.capacity() * Float.BYTES, colorBuffer, gl.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(attrib, 3, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attrib);
        }

        // UV
        if (hasUvs) {
            uvId = vbo.get(nextIndex);
            nextIndex++;
            attrib = material.getAttributeLocation(gl, "uv");
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, uvId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, uvBuffer.capacity() * Float.BYTES, uvBuffer, gl.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(attrib, 2, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attrib);
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
        //gl.glEnableVertexAttribArray(0);
        //gl.glEnableVertexAttribArray(2);
    }

    public void render(GL3 gl) {
        gl.glDrawElements(gl.GL_TRIANGLES, elementCount, gl.GL_UNSIGNED_INT, 0);
    }

    public void unbind(GL3 gl) {
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(0);
    }

    public void easyRender(GL3 gl, Matrix4f modelView, Matrix4f projection) {
        bind(gl);
        material.use(gl);

        float[] fa = new float[16];
        modelView.get(fa);
        gl.glUniformMatrix4fv(material.getModelViewLocation(), 1, false, fa, 0);
        projection.get(fa);
        gl.glUniformMatrix4fv(material.getProjectionLocation(), 1, false, fa, 0);

        render(gl);
        unbind(gl);
    }
}
