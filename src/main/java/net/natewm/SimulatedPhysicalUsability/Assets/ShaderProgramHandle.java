package net.natewm.SimulatedPhysicalUsability.Assets;

import net.natewm.SimulatedPhysicalUsability.Synchronization.IDependent;

/**
 * Created by Nathan on 1/7/2017.
 */
public class ShaderProgramHandle implements IAsyncHandle, IDependent {
    @Override
    public boolean isReady() {
        return false;
    }

    public void dependencyReady(Object dependency) {
        if (dependency instanceof ShaderHandle) {
            // Add shader to shader program
            // If all needed shader programs are available, link shader program
        }
        else {
            // Throw exception
        }
    }
}
