package net.natewm.SimulatedPhysicalUsability.Assets;

import net.natewm.SimulatedPhysicalUsability.Rendering.Material;
import net.natewm.SimulatedPhysicalUsability.Synchronization.IDependent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 1/6/2017.
 */
public class MaterialHandle implements IAsyncHandle, IDependent {
    private Material material = null;
    List<TextureHandle> textureHandles = new ArrayList<>();

    public MaterialHandle() {
    }

    @Override
    public boolean isReady() {
        return material != null;
    }

    public void dependencyReady(Object dependency) {
    }

    public Material getMaterial() {
        return material;
    }

    void setMaterial(Material material) {
        this.material = material;
    }
}
