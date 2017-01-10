package net.natewm.SimulatedPhysicalUsability.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.Rendering.Texture;

/**
 * Created by Nathan on 1/9/2017.
 */
public class TextureHandle {
    Texture texture = null;

    public void set(TextureHandle textureHandle) {
        texture = textureHandle.texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
