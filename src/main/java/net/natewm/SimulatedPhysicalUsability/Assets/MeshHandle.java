package net.natewm.SimulatedPhysicalUsability.Assets;

import net.natewm.SimulatedPhysicalUsability.Rendering.Mesh;
import net.natewm.SimulatedPhysicalUsability.Synchronization.IDependent;

/**
 * Created by Nathan on 1/6/2017.
 */
public class MeshHandle implements IAsyncHandle, IDependent {
    MaterialHandle materialHandle;
    Mesh mesh = null;

    public MeshHandle() {
    }

    @Override
    public boolean isReady() {
        return mesh != null && materialHandle.isReady();
    }

    public void dependencyReady(Object dependency) {
        if (dependency instanceof MaterialHandle) {
            // Set mesh material
        }
        else {
            // Throw exception
        }
    }

    public Mesh getMesh() {
        return mesh;
    }

    void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
