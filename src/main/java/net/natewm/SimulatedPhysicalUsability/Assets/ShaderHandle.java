package net.natewm.SimulatedPhysicalUsability.Assets;

import net.natewm.SimulatedPhysicalUsability.Synchronization.IDependent;

/**
 * Created by Nathan on 1/7/2017.
 */
public class ShaderHandle implements IAsyncHandle, IDependent {
    @Override
    public boolean isReady() {
        return false;
    }

    public void dependencyReady(Object dependency) {
        // TODO: Handle dependency of shader source (loaded from disk by asset manager)
    }
}
