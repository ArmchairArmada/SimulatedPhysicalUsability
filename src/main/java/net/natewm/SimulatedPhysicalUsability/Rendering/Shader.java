package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderUtil;

/**
 * Created by Nathan on 1/3/2017.
 */
public class Shader {
    int shaderID;
    int shaderType;

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
        gl.glGetShaderiv(shaderID, gl.GL_COMPILE_STATUS, status, 0);
        if (status[0] == gl.GL_FALSE) {
            throw new Exception(ShaderUtil.getShaderInfoLog(gl, shaderID));
        }
    }


    public int getShaderID() {
        return shaderID;
    }


    public int getShaderType() {
        return shaderType;
    }


    public void dispose(GL3 gl) {
        gl.glDeleteShader(shaderID);
        shaderID = 0;
    }
}
