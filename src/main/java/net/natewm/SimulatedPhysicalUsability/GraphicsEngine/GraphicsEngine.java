package net.natewm.SimulatedPhysicalUsability.GraphicsEngine;

import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Rendering.*;
import net.natewm.SimulatedPhysicalUsability.Resources.Geometry;
import net.natewm.SimulatedPhysicalUsability.Resources.Image;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Nathan on 1/9/2017.
 */
public class GraphicsEngine {
    private IFrameEndReciever frameEndReciever;
    //private ActionHandler actionHandler = new ActionHandler();
    private Queue<IGraphicsAction> actions = new LinkedList<>();
    private Renderer renderer = new Renderer();
    private Matrix4f cameraMatrix = new Matrix4f();

    public GraphicsEngine() {
    }

    public void setFrameReciever(IFrameEndReciever frameEndReciever) {
        this.frameEndReciever = frameEndReciever;
    }

    public void init(GL3 gl) {
        renderer.init(gl);
    }

    private synchronized boolean isEmpty() {
        return actions.isEmpty();
    }

    private synchronized void add(IGraphicsAction action) {
        actions.add(action);
    }

    private synchronized IGraphicsAction remove() {
        return actions.remove();
    }

    public void render(GL3 gl) {
        renderer.render(gl, cameraMatrix);
    }

    public void processActions(GL3 gl) {
        IGraphicsAction action;
        boolean completed = false;

        while (!completed) {
            //System.out.println(actions.size());
            if (!isEmpty()) {
                action = remove();
                completed = action.doIt(gl);
            }
        }

        frameEndReciever.graphicsFrameEnded();
    }


    public void frameEnd() {
        add((GL3 gl) -> {
            return true;
        });
    }

    /**
     * Creates a material with the specified properties and stores it in the material handle.
     *  @param material
     * @param shaderProgram
     * @param textures
     * @param properties
     */
    public void createMaterial(MaterialHandle material, ShaderProgramHandle shaderProgram,
                               List<TextureHandle> textures, List<MaterialPropertyHandle> properties) {
        add((GL3 gl) -> {
            material.material = new Material(gl, shaderProgram.shaderProgram);

            for(TextureHandle texture : textures) {
                material.material.addTexture(gl, texture.texture);
            }

            for(MaterialPropertyHandle property : properties) {
                material.material.addProperty(gl, property.materialProperty);
            }

            return false;
        });
    }

    public void createMaterialProperty1f(MaterialPropertyHandle materialPropertyHandle, String name, float value) {
        add((GL3 gl) -> {
            materialPropertyHandle.materialProperty = new MaterialProperty1f(name, value);
            return false;
        });
    }

    public void createMaterialProperty4f(MaterialPropertyHandle materialPropertyHandle, String name, Vector4f value) {
        add((GL3 gl) -> {
            materialPropertyHandle.materialProperty = new MaterialProperty4f(name, value);
            return false;
        });
    }

    public void createMesh(MeshHandle meshHandle, Geometry geometry, MaterialHandle materialHandle) {
        add((GL3 gl) -> {
            try {
                meshHandle.mesh = new Mesh(gl, geometry, materialHandle.material);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public void createMeshRenderNode(MeshRenderNodeHandle meshRenderNodeHandle, MeshHandle meshHandle) {
        add((GL3 gl) -> {
            meshRenderNodeHandle.renderNode = new MeshRenderNode(meshHandle.mesh);
            return false;
        });
    }

    public void setRenderNodeTransform(RenderNodeHandle renderNodeHandle, Transform transform) {
        add((GL3 gl) -> {
            renderNodeHandle.renderNode.getTransform().set(transform);
            return false;
        });
    }

    public void setCameraMatrix(Matrix4f cameraMatrix) {
        add((GL3 gl) -> {
            this.cameraMatrix.set(cameraMatrix);
            return false;
        });
    }

    public void setRendererClearColor(float[] color) {
        add((GL3 gl) -> {
            renderer.setClearColor(color);
            return false;
        });
    }

    public void setRendererSetProjection(float fieldOfView, float width, float height, float near, float far) {
        add((GL3 gl) -> {
            renderer.setProjection(fieldOfView, width, height, near, far);
            return false;
        });
    }

    public void addNodeToRenderer(RenderNodeHandle renderNodeHandle) {
        add((GL3 gl) -> {
            renderer.add(renderNodeHandle.renderNode);
            return false;
        });
    }

    public void removeNodeFromRenderer(RenderNodeHandle renderNodeHandle) {
        add((GL3 gl) -> {
            renderer.remove(renderNodeHandle.renderNode);
            return false;
        });
    }

    public void createShader(ShaderHandle shaderHandle, int shaderType, String[] sourceCode) {
        add((GL3 gl) -> {
            try {
                shaderHandle.shader = new Shader(gl, shaderType, sourceCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public void createShaderProgram(ShaderProgramHandle shaderProgramHandle, ShaderHandle vertexShader, ShaderHandle fragmentShader) {
        add((GL3 gl) -> {
            try {
                shaderProgramHandle.shaderProgram = new ShaderProgram(gl, vertexShader.shader, fragmentShader.shader);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public void createTexture(TextureHandle textureHandle, Image image) {
        add((GL3 gl) -> {
            textureHandle.texture = new Texture(gl, image);
            return false;
        });
    }

    public void destroyShaderProgram(ShaderProgramHandle shaderProgramHandle) {
        add((GL3 gl) -> {
            shaderProgramHandle.shaderProgram.dispose(gl);
            shaderProgramHandle.shaderProgram = null;
            return false;
        });
    }

    public void destroyShader(ShaderHandle shaderHandle) {
        add((GL3 gl) -> {
            shaderHandle.shader.dispose(gl);
            shaderHandle.shader = null;
            return false;
        });
    }

    public void destroyTexture(TextureHandle textureHandle) {
        add((GL3 gl) -> {
            textureHandle.texture.dispose(gl);
            textureHandle.texture = null;
            return false;
        });
    }

    public void destroyMesh(MeshHandle meshHandle) {
        add((GL3 gl) -> {
            meshHandle.mesh.dispose(gl);
            meshHandle.mesh = null;
            return false;
        });
    }

    public void reshape(int x, int y, int width, int height) {
        add((GL3 gl) -> {
            renderer.reshape(gl, x, y, width, height);
            return false;
        });
    }
}
