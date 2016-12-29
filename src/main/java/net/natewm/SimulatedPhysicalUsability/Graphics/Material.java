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
public class Material {
    int programID;
    int modelViewLocation;
    int projectionLocation;
    ArrayList<Texture> textures = new ArrayList<>();
    ArrayList<Integer> textureID = new ArrayList<>();

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

        //mvpLocation = getUniformLocation(gl, "mvp");
        modelViewLocation = getUniformLocation(gl, "modelView");
        projectionLocation = getUniformLocation(gl, "projection");
    }

    public int getModelViewLocation() {
        return modelViewLocation;
    }

    public int getProjectionLocation() {
        return projectionLocation;
    }

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

    public void addTexture(GL3 gl, Texture texture) {
        textures.add(texture);
        textureID.add(getUniformLocation(gl, "texture"+textureID.size()));
    }

    public void use(GL3 gl) {
        gl.glUseProgram(programID);

        for (int i=0; i<textures.size(); i++) {
            gl.glUniform1i(textureID.get(i), i);
            gl.glActiveTexture(gl.GL_TEXTURE0 + i);
            gl.glBindTexture(gl.GL_TEXTURE_2D, textures.get(i).getTextureID());
        }
    }

    public int getAttributeLocation(GL3 gl, String name) {
        return gl.glGetAttribLocation(programID, name);
    }

    public int getUniformLocation(GL3 gl, String name) {
        return gl.glGetUniformLocation(programID, name);
    }
}
