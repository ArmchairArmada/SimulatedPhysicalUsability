package net.natewm.SimulatedPhysicalUsability.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.Rendering.Mesh;

/**
 * Created by Nathan on 1/9/2017.
 */
public class MeshHandle {
    Mesh mesh;

    public void set(MeshHandle meshHandle) {
        mesh = meshHandle.mesh;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
