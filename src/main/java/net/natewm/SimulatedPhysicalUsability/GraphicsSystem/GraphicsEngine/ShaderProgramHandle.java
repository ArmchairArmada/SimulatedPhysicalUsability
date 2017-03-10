package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.ShaderProgram;

/**
 * Handle for shader programs.
 */
public class ShaderProgramHandle {
    ShaderProgram shaderProgram = null;

    /**
     * Sets the shader program to match that of a given handle.
     *
     * @param shaderProgramHandle Shader program to use for setting the handle's shader program.
     */
    public void set(ShaderProgramHandle shaderProgramHandle) {
        shaderProgram = shaderProgramHandle.shaderProgram;
    }

    /**
     * Gets the shader program from the handle.
     *
     * @return The shader program.
     */
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    /**
     * Sets the shader program for the handle.
     *
     * @param shaderProgram Shader program.
     */
    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}
