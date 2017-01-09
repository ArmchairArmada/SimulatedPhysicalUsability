package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Rendering.Material;
import net.natewm.SimulatedPhysicalUsability.Rendering.Mesh;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 1/6/2017.
 */
public class Scene {
    /*
        Ideally, rendering will be conducted with the following organization:

        Render renderables:
            Rendering grouped by same mesh:
                Rendering grouped by same material:
                    Rendering group by same material properties:
                        Render object
     */

    private class MaterialGroup {
        List<SceneObject> sceneObjects = new ArrayList<>();
        Material material;

        public MaterialGroup(Material material) {
            this.material = material;
        }

        public void add(SceneObject sceneObject) {
            sceneObjects.add(sceneObject);
        }

        public void render(GL3 gl, Matrix4f cameraMatrix, Matrix4f projectionMatrix, float farPlane) {
            // TODO: Allow for changing material properties (grouping by property)
            material.use(gl);
            for (SceneObject sceneObject : sceneObjects) {
                sceneObject.render(gl, cameraMatrix, projectionMatrix, farPlane);
            }
        }

        public void remove(SceneObject sceneObject) {
            sceneObjects.remove(sceneObject);
        }
    }

    private class MeshGroup {
        List<MaterialGroup> materialGroups = new ArrayList<>();
        Map<Integer, Integer> indexMap = new HashMap<>();
        Mesh mesh;

        public MeshGroup(Mesh mesh) {
            this.mesh = mesh;
        }

        public void add(MeshSceneObject meshObject) {
            int id = meshObject.getMaterialId();
            if (!indexMap.containsKey(id)) {
                indexMap.put(id, materialGroups.size());
                materialGroups.add(new MaterialGroup(meshObject.getMaterial()));
            }

            materialGroups.get(indexMap.get(id)).add(meshObject);
        }

        public void render(GL3 gl, Matrix4f cameraMatrix, Matrix4f projectionMatrix, float farPlane) {
            mesh.bind(gl);
            for (MaterialGroup group : materialGroups) {
                group.render(gl, cameraMatrix, projectionMatrix, farPlane);
            }
            mesh.unbind(gl);
        }

        public void remove(SceneObject sceneObject) {
            for (MaterialGroup group : materialGroups) {
                group.remove(sceneObject);
            }
        }
    }

    private class RenderGroup {
        List<SceneObjectHandle> notReadyYet = new ArrayList<>();
        List<MeshGroup> meshGroups = new ArrayList<>();
        Map<Integer, Integer> meshIndexMap = new HashMap<>();

        public void add(SceneObjectHandle sceneObject) {
            if (sceneObject.isReady()) {
                switch (sceneObject.getType()) {
                    // TODO: Add more cases
                    case MESH: {
                        MeshSceneObject meshObject = (MeshSceneObject)sceneObject.getSceneObject();
                        int id = meshObject.getMeshId();
                        if (!meshIndexMap.containsKey(id)) {
                            meshIndexMap.put(id, meshGroups.size());
                            meshGroups.add(new MeshGroup(meshObject.getMesh()));
                        }

                        meshGroups.get(meshIndexMap.get(id)).add(meshObject);
                    }

                    default:
                        // Do nothing, I guess
                }
            }
            else {
                // Since the scene object is not ready yet, we don't know what kind of scene node it is.
                // We will try to add these again when they do become ready.
                notReadyYet.add(sceneObject);
            }
        }

        public void remove(SceneObjectHandle objectHandle) {
            if (notReadyYet.remove(objectHandle))
                return;

            SceneObject sceneObject = objectHandle.getSceneObject();
            for (MeshGroup group : meshGroups) {
                group.remove(sceneObject);
            }
        }

        public void processNotReadyObjects() {
            if (notReadyYet.isEmpty())
                return;

            List<SceneObjectHandle> toRemove = new ArrayList<>();
            for (SceneObjectHandle sceneObject : notReadyYet) {
                if (sceneObject.isReady()) {
                    toRemove.add(sceneObject);
                    add(sceneObject);
                }
            }
            notReadyYet.removeAll(toRemove);
        }

        public void render(GL3 gl, Matrix4f cameraMatrix, Matrix4f projectionMatrix, float farPlane) {
            for (MeshGroup group : meshGroups) {
                group.render(gl, cameraMatrix, projectionMatrix, farPlane);
            }
        }
    }

    private List<SceneObjectHandle> lights = new ArrayList<>();
    RenderGroup renderGroup = new RenderGroup();
    CameraSceneObject camera;
    float[] clearColor = {1f, 1f, 1f, 1f};

    public Scene() {
    }

    public void switchedTo(GL3 gl) {
        gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
    }

    public void setClearColor(GL3 gl, float[] color) {
        clearColor = color;
        gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
    }

    public void setCamera(SceneObjectHandle sceneObjectHandle) {
        camera = (CameraSceneObject)sceneObjectHandle.getSceneObject();
    }

    public void addSceneObject(SceneObjectHandle sceneObjectHandle) {
        renderGroup.add(sceneObjectHandle);
    }

    public void removeSceneObject(SceneObjectHandle sceneObjectHandle) {
        renderGroup.remove(sceneObjectHandle);
    }

    public void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        renderGroup.render(gl, camera.getCameraMatrix(), camera.getProjectionMatrix(), camera.getFarPlane());
    }
}
