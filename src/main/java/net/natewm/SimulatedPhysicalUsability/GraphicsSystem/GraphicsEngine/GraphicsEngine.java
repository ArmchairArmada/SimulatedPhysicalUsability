package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.*;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Geometry;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Image;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.ResourceManager;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The graphics engine is designed to run on it own thread, so actions are pushed to it to be performed.
 */
public class GraphicsEngine {
    private ResourceManager resourceManager = new ResourceManager();    // Manages resources (images, meshes, etc.)
    private IFrameEndReceiver frameEndReciever;                         // Notified when the frame rendering is done.
    private Queue<IGraphicsAction> actions = new LinkedList<>();        // Queue of actions to perform.
    private Renderer renderer = new Renderer();                         // Renderer for rendering the graphics.
    private Matrix4f cameraMatrix = new Matrix4f();                     // Matrix for camera's orientation.
    private GLCapabilities glCapabilities;                              // OpenGL capabilities.

    /**
     * Construct the graphics engine.
     */
    public GraphicsEngine() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL3); // OpenGL 3 will be used for this project.
        glCapabilities = new GLCapabilities(glProfile);     // Gets the capablities for this GL profile.
    }

    /**
     * Get's the OpenGL capabilities.
     *
     * @return Returns the GL capabilities.
     */
    public GLCapabilities getGlCapabilities() {
        return glCapabilities;
    }

    /**
     * Sets the frame receiver that the graphics engine should use.
     *
     * @param frameEndReceiver Receives a message when the frame rendering is over.
     */
    public void setFrameReceiver(IFrameEndReceiver frameEndReceiver) {
        this.frameEndReciever = frameEndReceiver;
    }

    /**
     * Initializes the graphics engine -- which initializes the renderer.
     *
     * @param gl OpenGL context to use.
     */
    public void init(GL3 gl) {
        renderer.init(gl);
    }

    /**
     * Checks if the action queue is empty.
     *
     * @return True if the action queue is empty, else false.
     */
    private synchronized boolean isEmpty() {
        return actions.isEmpty();
    }

    /**
     * Adds an action to the action queue.
     *
     * @param action Action to queue up.
     */
    private synchronized void add(IGraphicsAction action) {
        actions.add(action);
    }

    /**
     * Removes an action from the action queue.
     *
     * @return Action from the action queue.
     */
    private synchronized IGraphicsAction remove() {
        return actions.remove();
    }

    /**
     * Renders the scene.
     *
     * @param gl OpenGL context.
     */
    public void render(GL3 gl) {
        renderer.render(gl, cameraMatrix);
    }

    /**
     * Processes the actions in the action queue.
     *
     * @param gl OpenGL context.
     */
    public void processActions(GL3 gl) {
        IGraphicsAction action;
        boolean completed = false;

        // This will only process actions until the frame's actions are "completed"
        while (!completed) {
            if (!isEmpty()) {
                action = remove();
                completed = action.doIt(gl);
            }
        }

        // Frame has ended, so send a message to the frame end reciever.
        frameEndReciever.graphicsFrameEnded();
    }

    /**
     * Action to end the animation frame.
     */
    public void frameEnd() {
        add((GL3 gl) -> {
            return true;
        });
    }

    /**
     * Action to load mesh from a file.
     *
     * @param meshHandle     Handle to store mesh into.
     * @param materialHandle Handle to store material into.
     * @param filename       JSON file describing what mesh and material to load.
     */
    public void loadMesh(MeshHandle meshHandle, MaterialHandle materialHandle, String filename) {
        add((GL3 gl) -> {
            try {
                resourceManager.loadMesh(gl, meshHandle, materialHandle, filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    /**
     * Action to load a material from a file.
     *
     * @param materialHandle Handle to store material into.
     * @param filename       JSON file describing the material.
     */
    public void loadMaterial(MaterialHandle materialHandle, String filename) {
        add((GL3 gl) -> {
            try {
                resourceManager.loadMaterial(gl, materialHandle, filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    /**
     * Action to load a texture from a file.
     *
     * @param textureHandle Handle to store texture into.
     * @param filename      Image file of the texture to load. (PNG)
     */
    public void loadTexture(TextureHandle textureHandle, String filename) {
        add((GL3 gl) -> {
            try {
                resourceManager.loadTexture(gl, textureHandle, filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    /**
     * Action to create a material with the specified properties and stores it in the material handle.
     *
     * @param materialHandle Handle to store material into.
     * @param shaderProgram  Handle to store shader program into.
     * @param textures       List of handles for storing textures.
     * @param properties     Handle for storing material properties.
     */
    public void createMaterial(MaterialHandle materialHandle, ShaderProgramHandle shaderProgram,
                               List<TextureHandle> textures, List<MaterialPropertyHandle> properties) {
        add((GL3 gl) -> {
            Material material = new Material(gl, shaderProgram.shaderProgram);

            for (TextureHandle texture : textures) {
                material.addTexture(gl, texture.texture);
            }

            for (MaterialPropertyHandle property : properties) {
                material.addProperty(gl, property.materialProperty);
            }

            materialHandle.material = material;

            return false;
        });
    }

    /**
     * Action to replace a material's texture.
     *
     * @param materialHandle Handle of material to use.
     * @param textureHandle  Handle of texture to use.
     * @param number         The material's slot number for the texture to replace.
     */
    public void replaceMaterialTexture(MaterialHandle materialHandle, TextureHandle textureHandle, int number) {
        add((GL3 gl) -> {
            materialHandle.material.replaceTexture(gl, textureHandle.texture, number);
            return false;
        });
    }

    /**
     * Action to create a single float material property.
     *
     * @param materialPropertyHandle Handle to store material property in.
     * @param name                   Name of the material property.
     * @param value                  Value to use for the property.
     */
    public void createMaterialProperty1f(MaterialPropertyHandle materialPropertyHandle, String name, float value) {
        add((GL3 gl) -> {
            materialPropertyHandle.materialProperty = new MaterialProperty1f(name, value);
            return false;
        });
    }

    /**
     * Action to create a four float material property.
     *
     * @param materialPropertyHandle Handle to store material property in.
     * @param name                   Name of the material property.
     * @param value                  Values to use for the property.
     */
    public void createMaterialProperty4f(MaterialPropertyHandle materialPropertyHandle, String name, Vector4f value) {
        add((GL3 gl) -> {
            materialPropertyHandle.materialProperty = new MaterialProperty4f(name, value);
            return false;
        });
    }

    /**
     * Action to create a mesh from geometry.
     *
     * @param meshHandle Handle to store mesh into.
     * @param geometry   Geometry to construct mesh from.
     */
    public void createMesh(MeshHandle meshHandle, Geometry geometry) {
        add((GL3 gl) -> {
            try {
                meshHandle.mesh = new Mesh(gl, geometry);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    /**
     * Action to create a mesh render node.
     *
     * @param meshRenderNodeHandle Handle to store mesh render node into.
     * @param meshHandle           Handle for mesh to use.
     * @param materialHandle       Handle for material to use.
     */
    public void createMeshRenderNode(MeshRenderNodeHandle meshRenderNodeHandle, MeshHandle meshHandle,
                                     MaterialHandle materialHandle) {
        add((GL3 gl) -> {
            MeshRenderNode meshRenderNode = new MeshRenderNode(meshHandle.mesh, materialHandle.material);
            meshRenderNode.init(gl);
            meshRenderNodeHandle.renderNode = meshRenderNode;
            return false;
        });
    }

    /**
     * Action to set the transform of a render node.
     *
     * @param renderNodeHandle Handle of render node to set transform for.
     * @param transform        Transform to use with render node.
     */
    public void setRenderNodeTransform(RenderNodeHandle renderNodeHandle, Transform transform) {
        add((GL3 gl) -> {
            renderNodeHandle.renderNode.getTransform().set(transform);
            return false;
        });
    }

    /**
     * Action to set the camera's matrix.
     *
     * @param cameraMatrix Matrix to use for camera transform.
     */
    public void setCameraMatrix(Matrix4f cameraMatrix) {
        add((GL3 gl) -> {
            this.cameraMatrix.set(cameraMatrix);
            return false;
        });
    }

    /**
     * Action to set OpenGL's clear color.
     *
     * @param color Color to clear the screen with.
     */
    public void setRendererClearColor(float[] color) {
        add((GL3 gl) -> {
            renderer.setClearColor(gl, color);
            return false;
        });
    }

    /**
     * Action to set the renderer's projection.
     *
     * @param fieldOfView Field of view for the camera.
     * @param width       Width of the viewport.
     * @param height      Height of the viewport.
     * @param near        Near clipping plane distance.
     * @param far         Far clipping plane distance.
     */
    public void setRendererSetProjection(float fieldOfView, float width, float height, float near, float far) {
        add((GL3 gl) -> {
            renderer.setProjection(fieldOfView, width, height, near, far);
            return false;
        });
    }

    /**
     * Action to add a render node to the renderer.
     *
     * @param renderNodeHandle Handle to render node to add to renderer.
     */
    public void addNodeToRenderer(RenderNodeHandle renderNodeHandle) {
        add((GL3 gl) -> {
            renderer.add(renderNodeHandle.renderNode);
            return false;
        });
    }

    /**
     * Action to add a dynamic render node to renderer.  A dynamic render mode is something that might change rapidly,
     * which would make batching difficult.
     *
     * @param renderNodeHandle Handle of render node to add to renderer.
     */
    public void addDynamicNodeToRenderer(RenderNodeHandle renderNodeHandle) {
        add((GL3 gl) -> {
            renderNodeHandle.renderNode.setDynamic(true);
            renderer.add(renderNodeHandle.renderNode);
            return false;
        });
    }

    /**
     * Action to remove a render node from the renderer.
     *
     * @param renderNodeHandle Handle of the render node to remove from the renderer.
     */
    public void removeNodeFromRenderer(RenderNodeHandle renderNodeHandle) {
        add((GL3 gl) -> {
            renderer.remove(renderNodeHandle.renderNode);
            return false;
        });
    }

    /**
     * Action to remove all render nodes from the renderer.
     */
    public void removeAllNodesFromRenderer() {
        add((GL3 gl) -> {
            renderer.removeAll();
            return false;
        });
    }

    /**
     * Action to create a shader.
     *
     * @param shaderHandle Handle to store shander in.
     * @param shaderType   Type of shader (OpenGL's vertex or fragment shader).
     * @param sourceCode   GLSL source code for the shader.
     */
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

    /**
     * Action to create a shader program.
     *
     * @param shaderProgramHandle Handle to store shader program into.
     * @param vertexShader        Handle for vertex shader to use.
     * @param fragmentShader      Handle for fragment shader to use.
     */
    public void createShaderProgram(ShaderProgramHandle shaderProgramHandle, ShaderHandle vertexShader,
                                    ShaderHandle fragmentShader) {
        add((GL3 gl) -> {
            try {
                shaderProgramHandle.shaderProgram = new ShaderProgram(gl, vertexShader.shader, fragmentShader.shader);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    /**
     * Action to create a texture map.
     *
     * @param textureHandle Handle to store texture into.
     * @param image         Image to create texture map from.
     */
    public void createTexture(TextureHandle textureHandle, Image image) {
        add((GL3 gl) -> {
            textureHandle.texture = new Texture(gl, image);
            return false;
        });
    }

    /**
     * Action to create a texture map, with simple quality settings.
     *
     * @param textureHandle Handle to store texture into.
     * @param image         Image to create texture map from.
     * @param quality       True uses ansitropic filtering and mipmapping.
     */
    public void createTexture(TextureHandle textureHandle, Image image, boolean quality) {
        add((GL3 gl) -> {
            textureHandle.texture = new Texture(gl, image, quality);
            return false;
        });
    }

    /**
     * Action to create texture from a byte buffer.
     *
     * @param textureHandle Handle to store texture into.
     * @param byteBuffer    Byte buffer to create texture from.
     * @param width         Width of the texture to create.
     * @param height        Height of the texture to create.
     * @param quality       True uses ansitropic filtering and mipmapping.
     */
    public void createTexture(TextureHandle textureHandle, ByteBuffer byteBuffer, int width, int height,
                              boolean quality) {
        add((GL3 gl) -> {
            textureHandle.texture = new Texture(gl, byteBuffer, width, height, quality);
            return false;
        });
    }

    /**
     * Action to create texture from a byte buffer.
     *
     * @param textureHandle Handle to store texture into.
     * @param byteBuffer    Byte buffer to create texture from.
     * @param width         Width of the texture to create.
     * @param height        Height of the texture to create.
     */
    public void createTexture(TextureHandle textureHandle, ByteBuffer byteBuffer, int width, int height) {
        add((GL3 gl) -> {
            textureHandle.texture = new Texture(gl, byteBuffer, width, height);
            return false;
        });
    }

    /**
     * Action to set texture options for texture.
     *
     * @param textureHandle Handle of texture to set options for.
     * @param wrapS         Texture wrap S option.
     * @param wrapT         Texture wrap T option.
     * @param minFilter     Min filter option.
     * @param magFilter     Mag filter option.
     */
    public void setTextureOptions(TextureHandle textureHandle, int wrapS, int wrapT, int minFilter, int magFilter) {
        add((GL3 gl) -> {
            textureHandle.texture.setTextureOptions(gl, wrapS, wrapT, minFilter, magFilter);
            return false;
        });
    }

    /**
     * Action to set material options.
     *
     * @param materialHandle Handle of the material to set options for.
     * @param textureNumber  Material's texture number to set options for.
     * @param wrapS          Texture wrap S option.
     * @param wrapT          Texture wrap T option.
     * @param minFilter      Min filter option.
     * @param magFilter      Mag filter option.
     */
    public void setMaterialTextureOptions(MaterialHandle materialHandle, int textureNumber, int wrapS, int wrapT,
                                          int minFilter, int magFilter) {
        add((GL3 gl) -> {
            materialHandle.material.setTextureOptions(gl, textureNumber, wrapS, wrapT, minFilter, magFilter);
            return false;
        });
    }

    /**
     * Action Update a texture with a new byte buffer.
     *
     * @param textureHandle Handle for texture to update.
     * @param byteBuffer    Byte buffer to udpate texture with.
     */
    public void updateTexture(TextureHandle textureHandle, ByteBuffer byteBuffer) {
        add((GL3 gl) -> {
            textureHandle.texture.updateByteBuffer(gl, byteBuffer);
            return false;
        });
    }

    /**
     * Action to make a mesh node's material unique.  This means that this material will not be shared between nodes.
     *
     * @param meshRenderNodeHandle Handle of mesh render node to make a unique material for.
     */
    public void makeMeshNodeMaterialUnique(MeshRenderNodeHandle meshRenderNodeHandle) {
        add((GL3 gl) -> {
            ((MeshRenderNode) meshRenderNodeHandle.renderNode).makeUniqueMaterial();
            return false;
        });
    }

    /**
     * Action to set a mesh render node's texture.
     *
     * @param meshRenderNodeHandle Handle of mesh render node to set texture for.
     * @param textureHandle        Handle of texture this mesh render node should use.
     * @param number               Number of material's texture to replace.
     */
    public void setMeshNodeTexture(MeshRenderNodeHandle meshRenderNodeHandle, TextureHandle textureHandle, int number) {
        add((GL3 gl) -> {
            ((MeshRenderNode) meshRenderNodeHandle.renderNode).setTexture(gl, textureHandle.texture, number);
            return false;
        });
    }

    /**
     * Action to update a mesh render node's texture using a byte buffer.
     *
     * @param meshRenderNodeHandle Handle for mesh render node to update.
     * @param number               Material's texture number to update.
     * @param byteBuffer           Byte buffer for new texture.
     */
    public void updateMeshNodeByteBufferTexture(MeshRenderNodeHandle meshRenderNodeHandle, int number,
                                                ByteBuffer byteBuffer) {
        add((GL3 gl) -> {
            ((MeshRenderNode) meshRenderNodeHandle.renderNode).updateByteBufferTexture(gl, number, byteBuffer);
            return false;
        });
    }

    /**
     * Action to destroy a shader program.
     *
     * @param shaderProgramHandle Handle of shader program to destroy.
     */
    public void destroyShaderProgram(ShaderProgramHandle shaderProgramHandle) {
        add((GL3 gl) -> {
            ShaderProgram shaderProgram = shaderProgramHandle.shaderProgram;
            shaderProgramHandle.shaderProgram = null;
            shaderProgram.dispose(gl);
            return false;
        });
    }

    /**
     * Action to destroy a shader.
     *
     * @param shaderHandle Handle of shader to destroy.
     */
    public void destroyShader(ShaderHandle shaderHandle) {
        add((GL3 gl) -> {
            Shader shader = shaderHandle.shader;
            shaderHandle.shader = null;
            shader.dispose(gl);
            return false;
        });
    }

    /**
     * Action to destroy a texture.
     *
     * @param textureHandle Handle of texture to destroy.
     */
    public void destroyTexture(TextureHandle textureHandle) {
        add((GL3 gl) -> {
            Texture texture = textureHandle.texture;
            textureHandle.texture = null;
            texture.dispose(gl);
            return false;
        });
    }

    /**
     * Action to destroy a mesh.
     *
     * @param meshHandle Handle of mesh to destroy.
     */
    public void destroyMesh(MeshHandle meshHandle) {
        add((GL3 gl) -> {
            Mesh mesh = meshHandle.mesh;
            meshHandle.mesh = null;
            mesh.dispose(gl);
            return false;
        });
    }

    /**
     * Aciton to reshape the viewport.
     *
     * @param x      X position of viewport.
     * @param y      Y position of viewport.
     * @param width  Width of the viewport.
     * @param height Height of the viewport.
     */
    public void reshape(int x, int y, int width, int height) {
        add((GL3 gl) -> {
            renderer.reshape(gl, x, y, width, height);
            return false;
        });
    }

    /**
     * Action to destroy all resources.
     */
    public void disposeResources() {
        add((GL3 gl) -> {
            resourceManager.disposeAll(this);
            return false;
        });
    }
}
