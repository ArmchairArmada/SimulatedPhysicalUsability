package net.natewm.SimulatedPhysicalUsability.Graphics;

/**
 * Created by Nathan on 1/5/2017.
 */

import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Assets.*;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.Resources.Geometry;
import net.natewm.SimulatedPhysicalUsability.Resources.Image;
import net.natewm.SimulatedPhysicalUsability.Resources.MaterialDescription;

import java.util.LinkedList;
import java.util.Queue;

/*
    How should handling actions that cannot be immediately performed be handled?

    For example, createShaderProgram needs to have the vertex and fragments shaders ready in order for the program to
    be created.  But these shaders might not be immediately available, since they need to be loaded from disk by the
    asset loading thread first.

    The options are:

        - Wait for the necessary handles to be ready -- this would ensure that actions are performed in order, but it
          will eliminate concurrency benefits, as this thread will be doing nothing while the asset loading thread is
          retrieving assets from disk.

        - Use some sort of callback or listener to have the dependencies alert the dependant handler that they are
          ready.  For example, shaders alert shader program, which alerts material, which alerts mesh.  I just started
          thinking about synchronization issues -- I'm not sure how this might work.

        - More actions -- When a dependency handler is ready, it can put a new action on the action queue to alert
          a dependent that it is ready.  This can help split up complicated actions into a series of actions, which
          can have other actions interleaved between them.  Problem: how does a handler with multiple dependencies
          know what to alert and when?  For example, multiple meshes might need to use the same texture -- when the
          texture is ready, what will be put on the queue?  Maybe there can be some way of managing a dependency list?
          This can either be in the resource handle itself (which might waste a little memory after it is no longer
          needed), or the GraphicsEngine could have some sort of Map for managing dependency lists temporarily.
 */

// TODO: Move Action* classes into lambdas in this class (will make the code less spread out)

/**
 * Main interface for interacting with the graphics engine.  This will not be running on its own thread, due to the
 * GraphicsPanel managing OpenGL stuff through the GLEventListener, but it will be used in an asynchronous way.  That
 * is to say, actions will be queued up and then processed when they will be most appropriate to.
 */
public class GraphicsEngine {
    private GL3 gl;
    private Renderer renderer = new Renderer();
    private Queue<IAction> actions = new LinkedList<IAction>();

    public GraphicsEngine() {
    }

    public void init(GL3 gl) {
        this.gl = gl;
    }

    public void updateFrame() {
        renderer.render(gl);
        processActions();
    }

    private void processActions() {
        IAction action;
        boolean frameEnded = false;

        // This will process all action until it encounters a frameEnd action.
        while (!frameEnded) {
            if (!isEmpty()) {
                action = removeAction();
                frameEnded = action.doIt(gl, this);
            }
        }
    }

    Renderer getRenderer() {
        return renderer;
    }

    // Rendering
    public void frameEnd(IGraphicsEngineMessageReciever reciever) {
        addAction((GL3 gl, GraphicsEngine engine) -> {
            return true;
        });
    }

    // Scene
    public void switchScene(SceneHandle scene) {
        addAction((GL3 gl, GraphicsEngine engine) -> {
            renderer.setScene(scene.getScene());
            return false;
        });
    }

    public void setSceneClearColor(SceneHandle scene, float[] color) {
        addAction((GL3 gl, GraphicsEngine engine) -> {
            scene.getScene().setClearColor(gl, color);
            return false;
        });
    }

    // Scene Objects
    public void transformSceneObject(SceneObjectHandle sceneObject, Transform transform) {
        addAction((GL3 gl, GraphicsEngine engine) -> {
            sceneObject.getSceneObject().setTransform(new Transform(transform));
            return false;
        });
    }

    public void setMaterialProperty(SceneObjectHandle sceneObject, MaterialPropertyHandle property, float a) {
        // TODO: Set material property 1
    }

    public void setMaterialProperty(SceneObjectHandle sceneObject, MaterialPropertyHandle property, float a, float b) {
        // TODO: Set material property 2
    }

    public void setMaterialProperty(SceneObjectHandle sceneObject, MaterialPropertyHandle property, float a, float b, float c) {
        // TODO: Set material property 3
    }

    public void setMaterialProperty(SceneObjectHandle sceneObject, MaterialPropertyHandle property, float a, float b, float c, float d) {
        // TODO: Set material property 4
    }

    // Scene and Scene Objects
    public void setSceneCamera(SceneHandle scene, SceneObjectHandle cameraHandle) {
        addAction((GL3 gl, GraphicsEngine engine) -> {
            scene.getScene().setCamera(cameraHandle);
            return false;
        });
    }

    public void addToScene(final SceneHandle scene, final SceneObjectHandle sceneObject) {
        addAction((GL3 gl, GraphicsEngine engine) -> {
            scene.getScene().addSceneObject(sceneObject);
            return false;
        });
    }

    public void removeFromScene(SceneHandle scene, SceneObjectHandle sceneObject) {
        addAction((GL3 gl, GraphicsEngine engine) -> {
            scene.getScene().removeSceneObject(sceneObject);
            return false;
        });
    }

    // Assets
    public void createTexture(TextureHandle textureHandle, Image image) {
        // TODO: Create texture
    }

    public void createMaterial(MaterialHandle materialHandle, ShaderProgramHandle shaderProgramHandle, MaterialDescription materialDescription) {
        // TODO: Create material
    }

    public void createMesh(MeshHandle meshHandle, MaterialHandle materialHandle, Geometry geometry) {
        // TODO: Create mesh
    }

    public void createShader(ShaderHandle shaderHandle, int shaderType, String[] sourceCode) {
        // TODO: Create shader
    }

    public void createShaderProgram(ShaderProgramHandle shaderProgramHandle, ShaderHandle vertexShaderHandle, ShaderHandle fragmentShaderHandle) {
        // TODO: Create shader program
    }

    private synchronized boolean isEmpty() {
        return actions.isEmpty();
    }

    private synchronized void addAction(IAction action) {
        actions.add(action);
    }

    private synchronized IAction removeAction() {
        return actions.remove();
    }
}
