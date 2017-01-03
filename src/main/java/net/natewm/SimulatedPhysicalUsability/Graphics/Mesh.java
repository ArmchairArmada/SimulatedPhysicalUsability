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

/**
 * OpenGL mesh
 */
public class Mesh {
    IntBuffer vao;      // Vertex array object id
    IntBuffer vbo;      // Vertex buffer object id

    Material material;  // Material to use with mesh

    int triangleID = 0; // ID for OpenGL buffer containing triangle vertex indices
    int vertexId = 0;   // ID for OpenGL buffer containing vertex position values
    int normalId = 0;   // ID for OpenGL buffer containing vertex normal values
    int colorId = 0;    // ID for OpenGL buffer containing vertex color values
    int uvId = 0;       // ID for OpenGL buffer containing texture coordinate values

    boolean hasNormals; // If the mesh has vertex normal vectors
    boolean hasColors;  // If the mesh has vertex color values
    boolean hasUvs;     // If the mesh has vertex texture coordinates

    int triangleCount;  // Number of triangles in the mesh
    int vertexCount;    // Number of vertices in the mesh
    int elementCount;   // Number of elements in mesh (for glDrawElements)

    float radius;       // Radius of a sphere that mesh can fully fit inside

    /**
     * Constructor for creating an OpenGL mesh from a given geometry and material.
     *
     * @param gl       OpenGL
     * @param geometry Geometry to create mesh from
     * @param material Material for the geometry to use
     * @throws Exception Thrown if geometry is not consistent (same number of vertice properties as vertices)
     */
    public Mesh(GL3 gl, Geometry geometry , Material material) throws Exception {
        if (!geometry.checkConsistancy())
            throw new Exception("Inconsistant Geometry Buffer");

        this.material = material;

        hasNormals = geometry.normalsCount() > 0;
        hasColors = geometry.colorsCount() > 0;
        hasUvs = geometry.uvCount() > 0;

        triangleCount = geometry.trianglesCount();
        vertexCount = geometry.vertexCount();
        elementCount = triangleCount * 3;

        genVAO(gl, geometry);
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
     * @param geometry Gemetry to create vertex array object with
     */
    private void genVAO(GL3 gl, Geometry geometry) {
        FloatBuffer vertexBuffer = geometry.makeVertexBuffer();
        FloatBuffer normalBuffer = geometry.makeNormalBuffer();
        FloatBuffer colorBuffer = geometry.makeColorBuffer();
        FloatBuffer uvBuffer = geometry.makeUvBuffer();
        IntBuffer triangleBuffer = geometry.makeTriangleBuffer();
        radius = geometry.getRadius();
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
        gl.glDeleteVertexArrays(vao.capacity(), vao);
    }

    /**
     * Bind this mesh to OpenGL
     *
     * @param gl OpenGL
     */
    public void bind(GL3 gl) {
        gl.glBindVertexArray(vao.get(0));
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, triangleID);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vertexId);
        //gl.glEnableVertexAttribArray(0);
        //gl.glEnableVertexAttribArray(2);
        material.use(gl);
    }

    /**
     * Bind the matrices to OpenGL necessary for rendering this mesh.
     *
     * @param gl         OpenGL
     * @param modelView  Model View matrix
     * @param projection Projection matrix
     */
    public void bindMatricies(GL3 gl, Matrix4f modelView, Matrix4f projection) {
        float[] fa = new float[16];
        modelView.get(fa);
        gl.glUniformMatrix4fv(material.getModelViewLocation(), 1, false, fa, 0);
        projection.get(fa);
        gl.glUniformMatrix4fv(material.getProjectionLocation(), 1, false, fa, 0);
    }

    /**
     * Renders the mesh using OpenGL
     *
     * @param gl OpenGL
     */
    public void render(GL3 gl) {
        gl.glDrawElements(gl.GL_TRIANGLES, elementCount, gl.GL_UNSIGNED_INT, 0);
    }

    /**
     * Unbind stuff from OpenGL
     *
     * @param gl OpenGL
     */
    public void unbind(GL3 gl) {
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(0);
    }

    /**
     * Simpler, though less efficient rendering, by binding and unbinding everything that is needed.
     *
     * @param gl         OpenGL
     * @param modelView  Model view matrix
     * @param projection Projection matrix
     */
    public void easyRender(GL3 gl, Matrix4f modelView, Matrix4f projection) {
        bind(gl);
        bindMatricies(gl, modelView, projection);
        render(gl);
        unbind(gl);
    }
}
