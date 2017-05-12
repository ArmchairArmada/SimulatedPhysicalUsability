package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import com.sun.prism.impl.BufferUtil;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Geometry;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * OpenGL mesh
 */
public class Mesh {
    /**
     * Defines a region of mesh data where a submesh exists.
     */
    public class SubMeshInfo {
        final int vertexStart;
        final int vertexCount;
        final int elementStart;
        final int elementEnd;
        final int elementCount;

        SubMeshInfo(int vertexStart, int vertexCount, int elementStart, int elementCount) {
            this.vertexStart = vertexStart;
            this.vertexCount = vertexCount;
            this.elementStart = elementStart;
            this.elementEnd = elementStart + elementCount;
            this.elementCount = elementCount;
        }
    }

    private int vao;            // Vertex array object id
    private IntBuffer vbo;      // Vertex buffer object id

    private int triangleID = -1; // ID for OpenGL buffer containing triangle vertex indices
    private int vertexId = -1;   // ID for OpenGL buffer containing vertex position values
    private int normalId = -1;   // ID for OpenGL buffer containing vertex normal values
    private int colorId = -1;    // ID for OpenGL buffer containing vertex color values
    private int uvId = -1;       // ID for OpenGL buffer containing texture coordinate values

    boolean hasNormals; // If the mesh has vertex normal vectors
    private boolean hasColors;  // If the mesh has vertex color values
    boolean hasUvs;     // If the mesh has vertex texture coordinates

    private SubMeshInfo[] subMeshInfos;

    float radius;       // Radius of a sphere that mesh can fully fit inside

    /**
     * Constructor for creating an OpenGL mesh from a given geometry and material.
     *
     * @param gl       OpenGL
     * @param geometry Geometry to create mesh from
     * @throws Exception Thrown if geometry is not consistent (same number of vertices properties as vertices)
     */
    public Mesh(GL3 gl, Geometry geometry) throws Exception {
        if (!geometry.checkConsistency())
            throw new Exception("Inconsistent Geometry Buffer");

        hasNormals = geometry.normalsCount() > 0;
        hasColors = geometry.colorsCount() > 0;
        hasUvs = geometry.uvCount() > 0;

        subMeshInfos = new SubMeshInfo[geometry.getSubGeometryCount()];
        for (int i=0; i<geometry.getSubGeometryCount(); i++) {
            Geometry.SubGeometryInfo subGeometryInfo = geometry.getSubGeometryInfo(i);
            subMeshInfos[i] = new SubMeshInfo(
                    subGeometryInfo.verticesStart,
                    subGeometryInfo.verticesLength,
                    subGeometryInfo.trianglesStart * 3,
                    subGeometryInfo.trianglesLength * 3
            );
        }

        genVAO(gl, geometry);
    }

    /**
     * Gets the vertex array object ID..
     *
     * @return Vertex array object ID.
     */
    public int getVao() {
        return vao;
    }

    /**
     * Gets a vertex buffer ID.
     *
     * @return Vertex buffer ID.
     */
    public int getVertexId() {
        return vertexId;
    }

    /**
     * Gets a normal buffer ID.
     *
     * @return Normal buffer ID.
     */
    public int getNormalId() {
        return normalId;
    }

    /**
     * Gets a color buffer ID.
     *
     * @return Color buffer ID.
     */
    public int getColorId() {
        return colorId;
    }

    /**
     * Gets a UV texture coordinate buffer ID.
     *
     * @return UV coordinate buffer ID.
     */
    public int getUvId() {
        return uvId;
    }

    /**
     * Counts the number of buffers that need to be created for OpenGL.
     *
     * @return The number of buffers to create.
     */
    private int countEnabledBuffers() {
        int i = 2;  // Triangles and vertices;
        if (hasNormals) i++;
        if (hasColors) i++;
        if (hasUvs) i++;
        return i;
    }

    /**
     * Creates OpenGL vertex array object.
     *
     * @param gl       OpenGL
     * @param geometry Geometry to create vertex array object with
     */
    private void genVAO(GL3 gl, Geometry geometry) {
        FloatBuffer vertexBuffer = geometry.makeVertexBuffer();
        FloatBuffer normalBuffer = geometry.makeNormalBuffer();
        FloatBuffer colorBuffer = geometry.makeColorBuffer();
        FloatBuffer uvBuffer = geometry.makeUvBuffer();
        IntBuffer triangleBuffer = geometry.makeTriangleBuffer();
        radius = geometry.getRadius();
        int nextIndex = 2;
        int enabledBuffers = countEnabledBuffers();

        IntBuffer vaoBuffer = BufferUtil.newIntBuffer(1);
        vbo = Buffers.newDirectIntBuffer(enabledBuffers);

        gl.glGenVertexArrays(1, vaoBuffer);
        vao = vaoBuffer.get(0);
        gl.glBindVertexArray(vao);

        gl.glGenBuffers(enabledBuffers, vbo);
        triangleID = vbo.get(0);
        vertexId = vbo.get(1);

        // TRIANGLES
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, triangleID);
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, triangleBuffer.capacity() * Integer.BYTES, triangleBuffer, GL3.GL_STATIC_DRAW);

        // VERTEX POSITIONS
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexId);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL3.GL_STATIC_DRAW);

        // NORMALS
        if (hasNormals) {
            normalId = vbo.get(nextIndex);
            nextIndex++;
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, normalId);
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, normalBuffer.capacity() * Float.BYTES, normalBuffer, GL3.GL_STATIC_DRAW);
        }

        // COLORS
        if (hasColors) {
            colorId = vbo.get(nextIndex);
            nextIndex++;
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, colorId);
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, colorBuffer.capacity() * Float.BYTES, colorBuffer, GL3.GL_STATIC_DRAW);
        }

        // UV
        if (hasUvs) {
            uvId = vbo.get(nextIndex);
            //nextIndex++;
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, uvId);
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, uvBuffer.capacity() * Float.BYTES, uvBuffer, GL3.GL_STATIC_DRAW);
        }
    }

    /**
     * Destroys vertex array attribute.
     *
     * @param gl OpenGL
     */
    public void dispose(GL3 gl) {
        gl.glBindVertexArray(vbo.get(0));
        gl.glDisableVertexAttribArray(0);
        gl.glBindVertexArray(0);

        gl.glDeleteBuffers(vbo.capacity(), vbo);

        int[] vaoArray = {vao};
        gl.glDeleteVertexArrays(1, vaoArray, 0);
    }

    /**
     * Bind this mesh to OpenGL
     *
     * @param gl OpenGL
     */
    public void bind(GL3 gl) {
        gl.glBindVertexArray(vao);
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, triangleID);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexId);
    }

    /**
     * Bind the matrices to OpenGL necessary for rendering this mesh.
     *
     * @param gl         OpenGL
     * @param modelView  Model View matrix
     * @param projection Projection matrix
     */
    public void bindMatrices(GL3 gl, int modelViewLocation, int projectionLocation, Matrix4f modelView, Matrix4f projection) {
        float[] fa = new float[16];
        modelView.get(fa);
        gl.glUniformMatrix4fv(modelViewLocation, 1, false, fa, 0);
        projection.get(fa);
        gl.glUniformMatrix4fv(projectionLocation, 1, false, fa, 0);
    }

    /**
     * Renders the mesh using OpenGL
     *
     * @param gl OpenGL
     */
    public void render(GL3 gl, int levelOfDetail) {
        levelOfDetail = Math.max(Math.min(levelOfDetail, subMeshInfos.length-1), 0);
        SubMeshInfo subMeshInfo = subMeshInfos[levelOfDetail];
        gl.glDrawRangeElements(GL3.GL_TRIANGLES, subMeshInfo.vertexStart, subMeshInfo.vertexStart+subMeshInfo.vertexCount, subMeshInfo.elementCount, GL3.GL_UNSIGNED_INT, 4*subMeshInfo.elementStart);
    }

    /**
     * Unbind stuff from OpenGL
     *
     * @param gl OpenGL
     */
    public void unbind(GL3 gl) {
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(0);
    }
}
