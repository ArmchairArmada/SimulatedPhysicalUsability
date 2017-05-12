package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Interface for render nodes in the renderer.  Render nodes are things that can be drawn into the scene.
 */
public interface IRenderNode {
    /**
     * Gets the transform for the render node.
     *
     * @return Transform for orientation and position.
     */
    Transform getTransform();

    /**
     * Initializes the render node.
     *
     * @param gl OpenGL context.
     */
    void init(GL3 gl);

    /**
     * Sets if the render node is dynamic (whether it can be batched or not).
     *
     * @param dynamic True if not batched, false if it can be batched.
     */
    void setDynamic(boolean dynamic);

    /**
     * Returns if the render node is dynamic.
     *
     * @return True if dynamic, else false.
     */
    boolean isDynamic();

    /**
     * Gets the rendering group ID.  Render groups are used to group render nodes with similar properties (materials,
     * meshes, etc.) so that they can be batched and speed up rendering.
     *
     * @return Rendering group ID>
     */
    int getGroupID();

    /**
     * Binds this render node (binding mesh, materials, material properties, shaders, textures, etc.)
     *
     * @param gl OpenGL context.
     */
    void bind(GL3 gl);

    /**
     * Unbinds the render node.
     *
     * @param gl OpenGL context.
     */
    void unbind(GL3 gl);

    /**
     * Renders the render node.
     *
     * @param gl            OpenGL context.
     * @param modelView     Model and view matrix transformation.
     * @param projection    Projection matrix.
     * @param levelOfDetail Level of detail for the render node (as determined by distance) -- lower is higher quality.
     */
    void render(GL3 gl, Matrix4f modelView, Matrix4f projection, int levelOfDetail);

    /**
     * Gets the radius of a bounding sphere that the render node is fully contained within.
     *
     * @return Radius of bounding sphere.
     */
    float getRadius();

    /**
     * Updates the view matrix with the given camera and projection matrices.
     *
     * @param camera     Camera matrix.
     * @param projection Projection matrix.
     */
    void updateView(Matrix4f camera, Matrix4f projection);

    /**
     * Gets the model-view matrix for the render node.
     *
     * @return Model-view matrix.
     */
    Matrix4f getModelView();

    /**
     * Gets a point for the center of the bounding sphere in the view after transforming with camera and perspective.
     *
     * @return Position of bounding sphere's center point in the view.
     */
    Vector4f getViewCenter();

    /**
     * Gets the radius of the bounding sphere after transforming with camera and perspective.
     *
     * @return Radius of bounding sphere after in the view.
     */
    float getViewRadius();

    /**
     * Gets the Z distance of the bounding sphere after transforming with the camera and perspective.
     *
     * @return Z distance of bounding sphere center.
     */
    float getViewZ();

}
