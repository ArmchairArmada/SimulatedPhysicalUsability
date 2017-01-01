package net.natewm.SimulatedPhysicalUsability.Graphics;

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
    int programID;          // OpenGL program ID
    int modelViewLocation;  // Shader's uniform location for modelView matrix
    int projectionLocation; // Shader's uniform location for projection matrix
    ArrayList<Texture> textures = new ArrayList<>();            // List of textures the shader will use
    ArrayList<Integer> textureLocations = new ArrayList<>();    // List of texture uniform locations

    /**
     * Constructor for createing a Material.
     *
     * @param gl                 OpenGL
     * @param vertexShaderCode   GLSL source code for vertex shader.
     * @param fragmentShaderCode GLSL source code for fragment shader.
     */
    public Material(GL3 gl, String[] vertexShaderCode, String[] fragmentShaderCode) {
        int vertexShader = gl.glCreateShader(gl.GL_VERTEX_SHADER);
        int fragmentShader = gl.glCreateShader(gl.GL_FRAGMENT_SHADER);
        int[] codeLength;

        codeLength = new int[vertexShaderCode.length];
        for (int i=0; i<vertexShaderCode.length; i++) {
            codeLength[i] = vertexShaderCode[i].length();
        }
        gl.glShaderSource(vertexShader, codeLength.length, vertexShaderCode, codeLength, 0);

        codeLength = new int[fragmentShaderCode.length];
        for (int i=0; i<fragmentShaderCode.length; i++) {
            codeLength[i] = fragmentShaderCode[i].length();
        }
        gl.glShaderSource(fragmentShader, codeLength.length, fragmentShaderCode, codeLength, 0);

        gl.glCompileShader(vertexShader);

        // TODO: Better error checking and logging
        System.out.println(ShaderUtil.getShaderInfoLog(gl, vertexShader));

        gl.glCompileShader(fragmentShader);

        // TODO: Better error checking and logging
        System.out.println(ShaderUtil.getShaderInfoLog(gl, fragmentShader));

        programID = gl.glCreateProgram();
        gl.glAttachShader(programID, vertexShader);
        gl.glAttachShader(programID, fragmentShader);
        gl.glLinkProgram(programID);
        gl.glValidateProgram(programID);

        // TODO: Better error checking and logging
        System.out.println(ShaderUtil.getProgramInfoLog(gl, programID));

        modelViewLocation = getUniformLocation(gl, "modelView");
        projectionLocation = getUniformLocation(gl, "projection");
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
     * Loads a material's shaders from a file.
     *
     * @param gl             OpenGL
     * @param vertexSource   File name for vertex shader.
     * @param fragmentSource File name for fragment shader.
     * @return New Material
     * @throws IOException If a file is not found.
     */
    public static Material loadFromFiles(GL3 gl, String vertexSource, String fragmentSource) throws IOException {
        List<String> lines;
        String[] vertexShaderCode;
        String[] fragmentShaderCode;

        lines = Files.readAllLines(Paths.get(vertexSource), StandardCharsets.UTF_8);
        vertexShaderCode = lines.toArray(new String[lines.size()]);
        for (int i=0; i<vertexShaderCode.length; i++) {
            vertexShaderCode[i] = vertexShaderCode[i].concat("\r\n");
        }

        lines = Files.readAllLines(Paths.get(fragmentSource), StandardCharsets.UTF_8);
        fragmentShaderCode = lines.toArray(new String[lines.size()]);
        for (int i=0; i<fragmentShaderCode.length; i++) {
            fragmentShaderCode[i] = fragmentShaderCode[i].concat("\r\n");
        }

        return new Material(gl, vertexShaderCode, fragmentShaderCode);
    }

    /**
     * Adds a texture to the material.  Textures are used in the order they are added.
     *
     * @param gl      OpenGL.
     * @param texture Texture to add.
     */
    public void addTexture(GL3 gl, Texture texture) {
        textures.add(texture);
        textureLocations.add(getUniformLocation(gl, "texture"+textureLocations.size()));
    }

    /**
     * Tells OpenGL to use this material.
     *
     * @param gl OpenGL
     */
    void use(GL3 gl) {
        gl.glUseProgram(programID);

        for (int i=0; i<textures.size(); i++) {
            gl.glUniform1i(textureLocations.get(i), i);
            gl.glActiveTexture(gl.GL_TEXTURE0 + i);
            gl.glBindTexture(gl.GL_TEXTURE_2D, textures.get(i).getTextureID());
        }
    }

    /**
     * Gets a shader's attribute location.
     *
     * @param gl   OpenGL
     * @param name Name of the attribute.
     * @return Attribute ID from shader.
     */
    int getAttributeLocation(GL3 gl, String name) {
        return gl.glGetAttribLocation(programID, name);
    }

    /**
     * Gets a shader's uniform location.
     *
     * @param gl   OpenGL
     * @param name Name of the uniform.
     * @return Uniform ID from shader.
     */
    int getUniformLocation(GL3 gl, String name) {
        return gl.glGetUniformLocation(programID, name);
    }
}
