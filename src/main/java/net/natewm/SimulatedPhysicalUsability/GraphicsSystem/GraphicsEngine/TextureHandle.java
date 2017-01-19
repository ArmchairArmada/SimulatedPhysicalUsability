package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Texture;

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

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
