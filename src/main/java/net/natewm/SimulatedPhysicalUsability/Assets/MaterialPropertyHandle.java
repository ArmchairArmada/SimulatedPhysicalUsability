package net.natewm.SimulatedPhysicalUsability.Assets;

import net.natewm.SimulatedPhysicalUsability.Rendering.MaterialProperty;

/**
 * Created by Nathan on 1/5/2017.
 */
public class MaterialPropertyHandle implements IAsyncHandle {
    private MaterialProperty property = null;

    public MaterialPropertyHandle() {
    }

    public MaterialProperty getProperty() {
        return property;
    }

    void setProperty(MaterialProperty property) {
        this.property = property;
    }

    @Override
    public boolean isReady() {
        return property != null;
    }
}
