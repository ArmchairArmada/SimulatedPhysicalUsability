package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.ShaderProgram;

/**
 * Created by Nathan on 1/9/2017.
 */
public class ShaderProgramHandle {
    ShaderProgram shaderProgram = null;

    public void set(ShaderProgramHandle shaderProgramHandle) {
        shaderProgram = shaderProgramHandle.shaderProgram;
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}
