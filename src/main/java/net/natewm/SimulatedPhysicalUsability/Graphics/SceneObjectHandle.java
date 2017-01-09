package net.natewm.SimulatedPhysicalUsability.Graphics;

import net.natewm.SimulatedPhysicalUsability.Assets.IAsyncHandle;
import net.natewm.SimulatedPhysicalUsability.Assets.MeshHandle;
import net.natewm.SimulatedPhysicalUsability.Synchronization.IDependent;

/**
 * Created by Nathan on 1/5/2017.
 */
public class SceneObjectHandle implements IAsyncHandle, IDependent {
    public enum Type {
        NONE,
        CAMERA,
        MESH,
        POINT_LIGHT,
        DIRECTIONAL_LIGHT,
        TEXT
    }

    SceneObject sceneObject = null;
    Type type = Type.NONE;

    public SceneObjectHandle(Type type) {
    }

    public Type getType() {
        return type;
    }

    void setType(Type type) {
        this.type = type;
    }

    public SceneObject getSceneObject() {
        return sceneObject;
    }

    void setSceneObject(SceneObject sceneObject) {
        this.sceneObject = sceneObject;
    }

    @Override
    public boolean isReady() {
        return sceneObject != null && sceneObject.isReady();
    }

    public void dependencyReady(Object dependency) {
    }
}
