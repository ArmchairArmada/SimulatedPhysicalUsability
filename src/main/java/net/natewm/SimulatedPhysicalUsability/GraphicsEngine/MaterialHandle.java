package net.natewm.SimulatedPhysicalUsability.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.Rendering.Material;

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
}
