package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

/**
 * OpenGL shader resource.
 */
public class Shader {
    private int shaderID;   // Shader ID
    private int shaderType; // Shader type (vertex or fragment)

    /**
     * Creates a shader.
     *
     * @param gl         OpenGL
     * @param shaderType Shader type (defined by OpenGL constant)
     * @param sourceCode Shader source code text
     * @throws Exception If shader cannot compile.
     */
    public Shader(GL3 gl, int shaderType, String[] sourceCode) throws Exception {
        shaderID = gl.glCreateShader(shaderType);
        shaderType = shaderType;

        int[] codeLength;

        codeLength = new int[sourceCode.length];
        for (int i=0; i<sourceCode.length; i++) {
            codeLength[i] = sourceCode[i].length();
        }
        gl.glShaderSource(shaderID, codeLength.length, sourceCode, codeLength, 0);
        gl.glCompileShader(shaderID);

        int status[] = new int[1];
        gl.glGetShaderiv(shaderID, GL2ES2.GL_COMPILE_STATUS, status, 0);
        if (status[0] == GL.GL_FALSE) {
            throw new Exception(ShaderUtil.getShaderInfoLog(gl, shaderID));
        }
    }

    /**
     * Gets the shader's ID.
     *
     * @return Shader ID
     */
    public int getShaderID() {
        return shaderID;
    }

    /**
     * Gets the shader type.
     *
     * @return Shader type (OpenGL constant)
     */
    public int getShaderType() {
        return shaderType;
    }

    /**
     * Dispose of the shader.
     *
     * @param gl OpenGL
     */
    public void dispose(GL3 gl) {
        gl.glDeleteShader(shaderID);
        shaderID = 0;
    }
}
