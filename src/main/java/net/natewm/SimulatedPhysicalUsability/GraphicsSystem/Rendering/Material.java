package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;

import java.util.ArrayList;

/**
 * Created by Nathan on 12/27/2016.
 */

// TODO: Loading material from file.
// TODO: Material properties.

/*
    Example of changes in bind:

    MaterialLoader ml = new JSONMaterialLoader();
    Material m = ml.load("material.json");
    m.setProperty("diffuseColor", new Vector3f(1.0f, 0.5f, 0.0f));  // Override what was in the material
    m.bind(gl);

    It would be nice to allow each render node to specify unique values for material properties.  For example, each
    instances of a mesh might be a different color.  If an asset manager is implemented, it would be easier to create
    materials that reuse shader programs.  Some additional thought may be needed.
 */

/**
 * A surface material to be used with OpenGL rendering.
 */
public class Material {
    ShaderProgram shaderProgram;
    int modelViewLocation;  // Shader's uniform location for modelView matrix
    int projectionLocation; // Shader's uniform location for projection matrix
    ArrayList<Texture> textures = new ArrayList<>();            // List of textures the shader will bind
    ArrayList<Integer> textureLocations = new ArrayList<>();    // List of texture uniform locations
    ArrayList<MaterialProperty> materialProperties = new ArrayList<>();

    public Material(GL3 gl, ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        shaderProgram.use(gl);
        modelViewLocation = shaderProgram.getUniformLocation(gl, "modelView");
        projectionLocation = shaderProgram.getUniformLocation(gl, "projection");
    }

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

    public void initAttributes(GL3 gl, Mesh mesh) {
        int attrib;

        // VERTEX POSITIONS
        attrib = shaderProgram.getAttributeLocation(gl, "position");
        //System.out.println(attrib);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, mesh.getVertexId());
        gl.glVertexAttribPointer(attrib, 3, gl.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(attrib);

        // NORMALS
        attrib = shaderProgram.getAttributeLocation(gl, "normal");
        //System.out.println(attrib);
        if (attrib > -1 && mesh.hasNormals) {
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, mesh.getNormalId());
            gl.glVertexAttribPointer(attrib, 3, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attrib);
        }

        /*
        // COLORS
        attrib = shaderProgram.getAttributeLocation(gl, "color");
        //System.out.println(attrib);
        if (attrib > -1 && mesh.hasColors) {
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, mesh.getColorId());
            gl.glVertexAttribPointer(attrib, 3, gl.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attrib);
        }
        */

        // UV
        attrib = shaderProgram.getAttributeLocation(gl, "uv");
        //System.out.println(attrib);
        if (attrib > -1 && mesh.hasUvs) {
            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, mesh.getUvId());
            gl.glVertexAttribPointer(attrib, 2, gl.GL_FLOAT, false, 0, 0);
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

    public void addProperty(GL3 gl, MaterialProperty property) {
        property.attach(gl, this);
        materialProperties.add(property);
    }

    /**
     * Tells OpenGL to bind this material.
     *
     * @param gl OpenGL
     */
    public void bind(GL3 gl) {
        shaderProgram.use(gl);

        for (int i=0; i<textures.size(); i++) {
            gl.glUniform1i(textureLocations.get(i), i);
            gl.glActiveTexture(gl.GL_TEXTURE0 + i);
            gl.glBindTexture(gl.GL_TEXTURE_2D, textures.get(i).getTextureID());
        }

        useProperties(gl);
    }

    public void useProperties(GL3 gl) {
        for (MaterialProperty materialProperty : materialProperties) {
            materialProperty.use(gl);
        }
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void replaceTexture(GL3 gl, Texture texture, int number) {
        textures.set(number, texture);
    }

    public void unbind(GL3 gl) {
        for (int i=0; i<textures.size(); i++) {
            gl.glUniform1i(textureLocations.get(i), i);
            gl.glActiveTexture(gl.GL_TEXTURE0 + i);
            gl.glBindTexture(gl.GL_TEXTURE_2D, 0);
        }
    }

    public void setTextureOptions(GL3 gl, int textureNumber, int wrapS, int wrapT, int minFilter, int magFilter) {
        textures.get(textureNumber).setTextureOptions(gl, wrapS, wrapT, minFilter, magFilter);
    }
}
