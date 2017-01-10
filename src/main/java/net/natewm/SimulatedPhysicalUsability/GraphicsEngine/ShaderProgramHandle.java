package net.natewm.SimulatedPhysicalUsability.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.Rendering.ShaderProgram;

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
}
