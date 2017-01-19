package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Mesh;

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

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
