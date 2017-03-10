package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Material;

/**
 * Handle for material.
 */
public class MaterialHandle {
    Material material = null;

    /**
     * Sets the material to match the given material handle.
     *
     * @param materialHandle Handle of material to set this handle with.
     */
    public void set(MaterialHandle materialHandle) {
        material = materialHandle.material;
    }

    /**
     * Gets the material for this handle.
     *
     * @return Material for this handle.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material for this handle.
     *
     * @param material Material this handle should use.
     */
    public void setMaterial(Material material) {
        this.material = material;
    }
}
