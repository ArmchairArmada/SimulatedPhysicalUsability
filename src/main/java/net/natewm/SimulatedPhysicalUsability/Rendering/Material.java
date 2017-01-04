package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 12/27/2016.
 */

// TODO: Loading material from file.
// TODO: Material properties.

/*
    Example of changes in use:

    MaterialLoader ml = new JSONMaterialLoader();
    Material m = ml.load("material.json");
    m.setProperty("diffuseColor", new Vector3f(1.0f, 0.5f, 0.0f));  // Override what was in the material
    m.use(gl);

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
    ArrayList<Texture> textures = new ArrayList<>();            // List of textures the shader will use
    ArrayList<Integer> textureLocations = new ArrayList<>();    // List of texture uniform locations
    ArrayList<MaterialProperty> materialProperties = new ArrayList<>();


    public Material(GL3 gl, ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        shaderProgram.use(gl);
        modelViewLocation = shaderProgram.getUniformLocation(gl, "modelView");
        projectionLocation = shaderProgram.getUniformLocation(gl, "projection");
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
     * Tells OpenGL to use this material.
     *
     * @param gl OpenGL
     */
    public void use(GL3 gl) {
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
}
