package net.natewm.SimulatedPhysicalUsability.Graphics;

import net.natewm.SimulatedPhysicalUsability.Assets.IAsyncHandle;
import net.natewm.SimulatedPhysicalUsability.Synchronization.IDependent;

/**
 * Created by Nathan on 1/5/2017.
 */
public class SceneHandle implements IAsyncHandle, IDependent {
    Scene scene = new Scene();

    public SceneHandle() {
    }

    public Scene getScene() {
        return scene;
    }

    void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public boolean isReady() {
        return scene != null;
    }

    @Override
    public void dependencyReady(Object dependency) {
        if (dependency instanceof SceneObjectHandle)
            scene.addSceneObject((SceneObjectHandle)dependency);  // Change method to not take handle?
    }
}
