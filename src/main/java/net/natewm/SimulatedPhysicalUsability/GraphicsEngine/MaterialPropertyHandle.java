package net.natewm.SimulatedPhysicalUsability.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.Rendering.MaterialProperty;

/**
 * Created by Nathan on 1/9/2017.
 */
public class MaterialPropertyHandle {
    MaterialProperty materialProperty = null;

    public void set(MaterialPropertyHandle materialPropertyHandle) {
        materialProperty = materialPropertyHandle.materialProperty;
    }

    public MaterialProperty getMaterialProperty() {
        return materialProperty;
    }
}
