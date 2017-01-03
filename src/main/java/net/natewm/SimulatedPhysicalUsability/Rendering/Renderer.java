package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;

import java.util.ArrayList;

/**
 * Created by Nathan on 12/29/2016.
 */

/**
 * Renders an OpenGL scene.
 */
public class Renderer {
    ArrayList<ArrayList<IRenderNode>> renderGroups = new ArrayList<>();

    Matrix4f projection = new Matrix4f();
    //Matrix4f modelView = new Matrix4f();

    /**
     * Constructor
     */
    public Renderer() {
    }

    /**
     * Creates a render group, which is a collection of objects with the same OpenGL settings.  This reduces OpenGL
     * configuration switching for a slight performance benefit.
     *
     * @return An ID for the newly created render group.
     */
    public int createRenderGroup() {
        renderGroups.add(new ArrayList<>());
        return renderGroups.size()-1;
    }

    /**
     * Sets the projection matrix.
     *
     * @param fieldOfView Camera's field of view
     * @param width       Width of the viewport
     * @param height      Height of the viewport
     * @param near        Near clipping plane distance
     * @param far         Far clipping plane distance
     */
    public void setProjection(float fieldOfView, float width, float height, float near, float far) {
        projection.setPerspective((float)Math.toRadians(fieldOfView), width / height, near, far);
    }

    /**
     * Adds a render node to a group.
     *
     * @param renderGroup Render group ID to add node to.
     * @param node        Render node to add.
     */
    public void add(int renderGroup, IRenderNode node) {
        renderGroups.get(renderGroup).add(node);
    }

    /**
     * Removes a render node from a group.
     *
     * @param renderGroup Render group ID to remove node from.
     * @param node        Render node to remove.
     */
    public void remove(int renderGroup, IRenderNode node) {
        renderGroups.get(renderGroup).remove(node);
    }

    /**
     * Renders the OpenGL scene.  This uses render groups to render all similar objects together.  Frustum culling is
     * used to not render any object that is fully off the screen.
     *
     * @param gl     OpenGL
     * @param camera Camera matrix
     */
    public void render(GL3 gl, Matrix4f camera) {
        for (ArrayList<IRenderNode> renderGroup : renderGroups) {
            if (renderGroup.size() > 0) {
                renderGroup.get(0).bind(gl);

                // This is being done in parallel, to speed up matrix multiplicaitons
                renderGroup.parallelStream().forEach((node) -> {
                    node.updateView(camera, projection);
                });

                // Two loops are used, since I could not get locking to work to prevent OpenGL conflicts.
                for (IRenderNode node : renderGroup) {
                    // TODO: Use camera's far clipping plane
                    if (node.getViewZ() < 1000f && node.getViewZ() > 0f
                            && node.getViewCenter().x+node.getViewRadius() > -1.0f
                            && node.getViewCenter().x-node.getViewRadius() < 1.0f
                            && node.getViewCenter().y+node.getViewRadius() > -1.0f
                            && node.getViewCenter().y-node.getViewRadius() < 1.0f) {
                        node.render(gl, node.getModelView(), projection);
                    }
                }
                renderGroup.get(0).unbind(gl);
            }
        }
    }
}
