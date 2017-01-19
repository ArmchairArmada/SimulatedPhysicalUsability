package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.MaterialProperty;

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

    public void setMaterialProperty(MaterialProperty materialProperty) {
        this.materialProperty = materialProperty;
    }
}
