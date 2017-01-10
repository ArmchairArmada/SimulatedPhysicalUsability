package net.natewm.SimulatedPhysicalUsability.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.Rendering.Shader;

/**
 * Created by Nathan on 1/9/2017.
 */
public class ShaderHandle {
    Shader shader = null;

    public void set(ShaderHandle shaderHandle) {
        shader = shaderHandle.shader;
    }

    public Shader getShader() {
        return shader;
    }
}
