package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;

import java.util.ArrayList;

/**
 * A surface material to be used with OpenGL rendering.
 */
public class Material {
    private ShaderProgram shaderProgram;
    private int modelViewLocation;  // Shader's uniform location for modelView matrix
    private int projectionLocation; // Shader's uniform location for projection matrix
    ArrayList<Texture> textures = new ArrayList<>();            // List of textures the shader will bind
    private ArrayList<Integer> textureLocations = new ArrayList<>();    // List of texture uniform locations
    private ArrayList<MaterialProperty> materialProperties = new ArrayList<>();

    /**
     * Constructor for creating a material with a given shader program.
     *
     * @param gl            OpenGL context.
     * @param shaderProgram The shader program the material should use.
     */
    public Material(GL3 gl, ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        shaderProgram.use(gl);
        modelViewLocation = shaderProgram.getUniformLocation(gl, "modelView");
        projectionLocation = shaderProgram.getUniformLocation(gl, "projection");
    }

    /**
     * Copy constructor to have this material copy a given material.
     *
     * @param material Material to copy.
     */
    public Material(Material material) {
        shaderProgram = material.shaderProgram;
        modelViewLocation = material.modelViewLocation;
        projectionLocation = material.projectionLocation;
        textures.addAll(material.textures);
        textureLocations.addAll(material.textureLocations);
        for (MaterialProperty property : materialProperties) {
            materialProperties.add(property.clone());
        }
    }

    /**
     * Initialize the material attributes for what I mesh needs to use (vertices, normals, UVs).
     *
     * @param gl   OpenGL context.
     * @param mesh Mesh to initialize material for.
     */
    public void initAttributes(GL3 gl, Mesh mesh) {
        int attrib;

        // VERTEX POSITIONS
        attrib = shaderProgram.getAttributeLocation(gl, "position");
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, mesh.getVertexId());
        gl.glVertexAttribPointer(attrib, 3, GL3.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(attrib);

        // NORMALS
        attrib = shaderProgram.getAttributeLocation(gl, "normal");
        if (attrib > -1 && mesh.hasNormals) {
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, mesh.getNormalId());
            gl.glVertexAttribPointer(attrib, 3, GL3.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attrib);
        }

        /*
        // COLORS
        attrib = shaderProgram.getAttributeLocation(gl, "color");
        if (attrib > -1 && mesh.hasColors) {
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, mesh.getColorId());
            gl.glVertexAttribPointer(attrib, 3, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attrib);
        }
        */

        // UV
        attrib = shaderProgram.getAttributeLocation(gl, "uv");
        if (attrib > -1 && mesh.hasUvs) {
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, mesh.getUvId());
            gl.glVertexAttribPointer(attrib, 2, GL3.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attrib);
        }
    }

    /**
     * Gets the model view uniform location.
     *
     * @return ModelView uniform location.
     */
    int getModelViewLocation() {
        return modelViewLocation;
    }

    /**
     * Gets the projection uniform location.
     *
     * @return Projection uniform location.
     */
    int getProjectionLocation() {
        return projectionLocation;
    }

    /**
     * Adds a texture to the material.  Textures are used in the order they are added.
     *
     * @param gl      OpenGL.
     * @param texture Texture to add.
     */
    public void addTexture(GL3 gl, Texture texture) {
        textures.add(texture);
        textureLocations.add(shaderProgram.getUniformLocation(gl, "texture"+textureLocations.size()));
    }

    /**
     * Adds a material property to the material.
     *
     * @param gl       OpenGL context.
     * @param property Material property to add to the material.
     */
    public void addProperty(GL3 gl, MaterialProperty property) {
        property.attach(gl, this);
        materialProperties.add(property);
    }

    /**
     * Tells OpenGL to bind this material.
     *
     * @param gl OpenGL context.
     */
    public void bind(GL3 gl) {
        shaderProgram.use(gl);

        for (int i=0; i<textures.size(); i++) {
            gl.glUniform1i(textureLocations.get(i), i);
            gl.glActiveTexture(GL3.GL_TEXTURE0 + i);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, textures.get(i).getTextureID());
        }

        useProperties(gl);
    }

    /**
     * Has OpenGL use all of the material properties associated with this material.
     *
     * @param gl OpenGL context.
     */
    public void useProperties(GL3 gl) {
        for (MaterialProperty materialProperty : materialProperties) {
            materialProperty.use(gl);
        }
    }

    /**
     * Gets the shader program associated with this material.
     *
     * @return Shader program.
     */
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void replaceTexture(GL3 gl, Texture texture, int number) {
        textures.set(number, texture);
    }

    /**
     * Unbinds the material and it's textures.
     *
     * @param gl OpenGL context.
     */
    public void unbind(GL3 gl) {
        for (int i=0; i<textures.size(); i++) {
            gl.glUniform1i(textureLocations.get(i), i);
            gl.glActiveTexture(GL3.GL_TEXTURE0 + i);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        }
    }

    /**
     * Sets the texture options for a texture used by the material.
     *
     * @param gl            OpenGL context.
     * @param textureNumber Number of the texture in the material to set options for.
     * @param wrapS         Texture S wrapping option.
     * @param wrapT         Texture T wrapping option.
     * @param minFilter     Texture min filter option.
     * @param magFilter     Texture mag filter option.
     */
    public void setTextureOptions(GL3 gl, int textureNumber, int wrapS, int wrapT, int minFilter, int magFilter) {
        textures.get(textureNumber).setTextureOptions(gl, wrapS, wrapT, minFilter, magFilter);
    }
}
