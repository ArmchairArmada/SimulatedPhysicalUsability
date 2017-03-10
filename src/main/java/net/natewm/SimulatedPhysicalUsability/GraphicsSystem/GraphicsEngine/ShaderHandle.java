package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Shader;

/**
 * Handle for shaders.
 */
public class ShaderHandle {
    Shader shader = null;

    /**
     * Sets the shader in the handle to match that of a given shader handle.
     *
     * @param shaderHandle Handle of the shader to use.
     */
    public void set(ShaderHandle shaderHandle) {
        shader = shaderHandle.shader;
    }

    /**
     * Gets the shader from the handle.
     *
     * @return Shader.
     */
    public Shader getShader() {
        return shader;
    }

    /**
     * Sets the handle's shader.
     *
     * @param shader Shader that the handle should use.
     */
    public void setShader(Shader shader) {
        this.shader = shader;
    }
}
