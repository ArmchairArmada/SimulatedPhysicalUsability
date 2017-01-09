package net.natewm.SimulatedPhysicalUsability.Assets;

import net.natewm.SimulatedPhysicalUsability.Rendering.Texture;
import net.natewm.SimulatedPhysicalUsability.Synchronization.IDependent;

/**
 * Created by Nathan on 1/7/2017.
 */
public class TextureHandle implements IAsyncHandle, IDependent {
    private Texture texture;

    @Override
    public boolean isReady() {
        return false;
    }

    public void dependencyReady(Object dependency) {
        // TODO: Handle Image dependency (loaded from asset manager)
        // Not necessary?  The asset manager can be given the TextureHandle for a loadTexture function.  It will load
        // an image and then queue up a CreateTexture action on the graphics engine thread.  This means that the iamge
        // dependency can be used immediately without needing to wait for it to become available.
    }
}
