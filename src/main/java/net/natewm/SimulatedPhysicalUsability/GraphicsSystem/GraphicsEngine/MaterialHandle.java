package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Material;

/**
 * Created by Nathan on 1/9/2017.
 */
public class MaterialHandle {
    Material material = null;

    public void set(MaterialHandle materialHandle) {
        material = materialHandle.material;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
