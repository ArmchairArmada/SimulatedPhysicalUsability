package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.MaterialProperty;

/**
 * Handle for material properties.
 */
public class MaterialPropertyHandle {
    MaterialProperty materialProperty = null;

    /**
     * Sets the material property to match that of a given material property.
     *
     * @param materialPropertyHandle Handle to use for setting the material property in the handle.
     */
    public void set(MaterialPropertyHandle materialPropertyHandle) {
        materialProperty = materialPropertyHandle.materialProperty;
    }

    /**
     * Gets the material property for the handle.
     *
     * @return Material Property.
     */
    public MaterialProperty getMaterialProperty() {
        return materialProperty;
    }

    /**
     * Sets the material property in the handle to be the given material property.
     *
     * @param materialProperty Material property the handle should use.
     */
    public void setMaterialProperty(MaterialProperty materialProperty) {
        this.materialProperty = materialProperty;
    }
}
