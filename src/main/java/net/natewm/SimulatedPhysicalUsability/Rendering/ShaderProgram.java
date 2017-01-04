package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

/**
 * Created by Nathan on 1/3/2017.
 */
public class ShaderProgram {
    private Shader vertexShader;
    private Shader fragmentShader;
    private int programID;

    public ShaderProgram(GL3 gl, Shader vertexShader, Shader fragmentShader) throws Exception {
        int status[] = new int[1];

        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;

        programID = gl.glCreateProgram();
        gl.glAttachShader(programID, vertexShader.getShaderID());
        gl.glAttachShader(programID, fragmentShader.getShaderID());
        gl.glLinkProgram(programID);

        gl.glGetProgramiv(programID, gl.GL_LINK_STATUS, status, 0);
        if (status[0] == gl.GL_FALSE) {
            throw new Exception(ShaderUtil.getProgramInfoLog(gl, programID));
        }

        gl.glValidateProgram(programID);

        gl.glGetProgramiv(programID, gl.GL_VALIDATE_STATUS, status, 0);
        if (status[0] == gl.GL_FALSE) {
            throw new Exception(ShaderUtil.getProgramInfoLog(gl, programID));
        }
    }

    public int getProgramID() {
        return programID;
    }

    int getAttributeLocation(GL3 gl, String name) {
        return gl.glGetAttribLocation(programID, name);
    }

    public int getUniformLocation(GL3 gl, String name) {
        return gl.glGetUniformLocation(programID, name);
    }

    public void use(GL3 gl) {
        gl.glUseProgram(programID);
    }

    public void dispose(GL3 gl) {
        gl.glDetachShader(programID, fragmentShader.getShaderID());
        gl.glDetachShader(programID, vertexShader.getShaderID());
        gl.glDeleteProgram(programID);
        programID = 0;
    }
}
