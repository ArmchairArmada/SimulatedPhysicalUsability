package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Mesh;

/**
 * Handle for dealing with meshes in the graphics engine.
 */
public class MeshHandle {
    Mesh mesh;

    /**
     * Sets the mesh in the mesh handle to match that of a given handle.
     *
     * @param meshHandle Handle to use to set the mesh from.
     */
    public void set(MeshHandle meshHandle) {
        mesh = meshHandle.mesh;
    }

    /**
     * Gets the mesh in the handle.
     *
     * @return The mesh.
     */
    public Mesh getMesh() {
        return mesh;
    }

    /**
     * Sets the mesh in the mesh handle.
     *
     * @param mesh The mesh the handle should use.
     */
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
