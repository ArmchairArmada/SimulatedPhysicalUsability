package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

/**
 * Created by Nathan on 12/29/2016.
 */

/**
 * Renders an OpenGL scene.
 */
public class Renderer {
    private ArrayList<ArrayList<IRenderNode>> renderGroups = new ArrayList<>();
    private float fieldOfView = 49.0f;
    private float nearPlane = 0.1f;
    private float farPlane = 256.0f;
    private float[] clearColor = {1.0f, 1.0f, 1.0f, 1.0f};

    private Matrix4f projection = new Matrix4f();

    /**
     * Constructor
     */
    public Renderer() {
    }

    public void init(GL3 gl) {
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glEnable(gl.GL_CULL_FACE);
        gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
    }

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(float fov) {
        fieldOfView = fov;
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public void setNearPlane(float near) {
        nearPlane = near;
    }

    public float getFarPlane() {
        return farPlane;
    }

    public void setFarPlane(float far) {
        farPlane = far;
    }

    public float[] getClearColor() {
        return clearColor;
    }

    public void setClearColor(float[] color) {
        clearColor = color;
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

    public void remove(IRenderNode node) {
        for (ArrayList<IRenderNode> renderGroup : renderGroups) {
            renderGroup.remove(node);
        }
    }

    /**
     * Renders the OpenGL scene.  This uses render groups to render all similar objects together.  Frustum culling is
     * used to not render any object that is fully off the screen.
     *
     * @param gl     OpenGL
     * @param camera Camera matrix
     */
    public void render(GL3 gl, Matrix4f camera) {
        float r;
        Vector4f center;
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

        for (ArrayList<IRenderNode> renderGroup : renderGroups) {
            if (renderGroup.size() > 0) {
                renderGroup.get(0).bind(gl);

                // This is being done in parallel, to speed up matrix multiplicaitons
                renderGroup.parallelStream().forEach((node) -> {
                    //node.getTransform().updateMatrix();
                    node.updateView(camera, projection);
                });

                // Two loops are used, since I could not get locking to work to prevent OpenGL conflicts.
                for (IRenderNode node : renderGroup) {
                    center = node.getViewCenter();
                    r = node.getViewRadius();
                    if (node.getViewZ()-r < farPlane && node.getViewZ()+r > nearPlane
                            && center.x+r > -1.0f
                            && center.x-r < 1.0f
                            && center.y+r > -1.0f
                            && center.y-r < 1.0f) {
                        node.render(gl, node.getModelView(), projection);
                    }
                }
                renderGroup.get(0).unbind(gl);
            }
        }
    }

    public void reshape(GL3 gl, int x, int y, int width, int height) {
        gl.glViewport(0, 0, width, height);
        setProjection(fieldOfView, width, height, nearPlane, farPlane);
    }
}
