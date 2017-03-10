package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Texture;

/**
 * Handle for textures.
 */
public class TextureHandle {
    Texture texture = null;

    /**
     * Sets the texture in the handle to match that of a given texture handle.
     *
     * @param textureHandle Handle to match.
     */
    public void set(TextureHandle textureHandle) {
        texture = textureHandle.texture;
    }

    /**
     * Gets the texture from the handle.
     *
     * @return Texture.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the texture for the handle.
     *
     * @param texture Texture that the handle should use.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
