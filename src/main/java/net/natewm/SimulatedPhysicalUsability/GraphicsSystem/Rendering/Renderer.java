package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders an OpenGL scene.
 */
public class Renderer {
    private ArrayList<IRenderNode> dynamicNodes = new ArrayList<>();
    private final ArrayList<ArrayList<IRenderNode>> renderGroups = new ArrayList<>();
    private final Map<Integer, Integer> renderIndexMap = new HashMap<>();
    private float fieldOfView = 49.0f;
    private float nearPlane = 1.0f; // This was raised to reduce tearing on my laptop
    private float farPlane = 512.0f;
    private float[] clearColor = {1.0f, 1.0f, 1.0f, 1.0f};
    private static final float levelOfDetailScale = 0.15f;  // TODO: Allow configuration (maybe consider a config.json file?)

    private final Matrix4f projection = new Matrix4f();

    /**
     * Constructor
     */
    public Renderer() {
    }

    /**
     * Initializes OpenGL default settings.
     *
     * @param gl OpenGL
     */
    public void init(GL3 gl) {
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        setClearColor(gl, clearColor);
    }

    /**
     * Gets the field of view angle.
     *
     * @return Field of view.
     */
    public float getFieldOfView() {
        return fieldOfView;
    }

    /**
     * Sets field of view angle.
     *
     * @param fov Field of view angle.
     */
    public void setFieldOfView(float fov) {
        fieldOfView = fov;
    }

    /**
     * Gets the near clipping plane distance.
     *
     * @return The near clipping plane distance.
     */
    public float getNearPlane() {
        return nearPlane;
    }

    /**
     * Sets the near plane clipping distance.
     *
     * @param near Near plane clipping distance.
     */
    public void setNearPlane(float near) {
        nearPlane = near;
    }

    /**
     * Gets the far plane clipping distance.
     *
     * @return Far plane clipping distance.
     */
    public float getFarPlane() {
        return farPlane;
    }

    /**
     * Sets the far plane clipping distance.
     *
     * @param far Far plane clipping distance.
     */
    public void setFarPlane(float far) {
        farPlane = far;
    }

    /**
     * Gets the clear color.
     *
     * @return Clear color array.
     */
    public float[] getClearColor() {
        return clearColor;
    }

    /**
     * Sets the clear color.
     *
     * @param gl    OpenGL
     * @param color New clear color
     */
    public void setClearColor(GL3 gl, float[] color) {
        clearColor = color;
        gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
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
     * @param node        Render node to add.
     */
    public void add(IRenderNode node) {
        if (node.isDynamic()) {
            dynamicNodes.add(node);
        }
        else {
            if (!renderIndexMap.containsKey(node.getGroupID())) {
                renderIndexMap.put(node.getGroupID(), renderGroups.size());
                renderGroups.add(new ArrayList<>());
            }
            renderGroups.get(renderIndexMap.get(node.getGroupID())).add(node);
        }
    }

    /**
     * Removes a render node from a group.
     *
     * @param node        Render node to remove.
     */
    public void remove(IRenderNode node) {
        if (node.isDynamic())
            dynamicNodes.remove(node);
        else
            renderGroups.get(renderIndexMap.get(node.getGroupID())).remove(node);
    }

    public void removeAll() {
        dynamicNodes.clear();
        for (List<IRenderNode> renderNodeList : renderGroups) {
            renderNodeList.clear();
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
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // This is being done in parallel, to speed up matrix multiplications
        dynamicNodes.parallelStream().forEach((node) -> {
            //node.getTransform().updateMatrix();
            node.updateView(camera, projection);
        });

        for (IRenderNode node : dynamicNodes) {
            // Two loops are used, since I could not get locking to work to prevent OpenGL conflicts.
            //center = node.getViewCenter();
            r = node.getViewRadius();
            // Frustum culling has been disabled due to large objects disappearing.
            /*if (node.getViewZ()-node.getRadius() < farPlane
                    && node.getViewZ()+node.getRadius() > 0
                    && center.x+r > -1.0f
                    && center.x-r < 1.0f
                    && center.y+r > -1.0f
                    && center.y-r < 1.0f) {*/
                node.bind(gl);
                node.render(gl, node.getModelView(), projection, (int)(levelOfDetailScale/r/node.getRadius()));
                node.unbind(gl);
            //}
        }

        // TODO: Reduce redundancies
        for (ArrayList<IRenderNode> renderGroup : renderGroups) {
            if (renderGroup.size() > 0) {
                renderGroup.get(0).bind(gl);

                // This is being done in parallel, to speed up matrix multiplications
                renderGroup.parallelStream().forEach((node) -> {
                    //node.getTransform().updateMatrix();
                    node.updateView(camera, projection);
                });

                // Two loops are used, since I could not get locking to work to prevent OpenGL conflicts.
                for (IRenderNode node : renderGroup) {
                    //center = node.getViewCenter();
                    r = node.getViewRadius();
                    // Likewise, furstum culling has been disabled.
                    /*if (node.getViewZ()-node.getRadius() < farPlane
                            && node.getViewZ()+node.getRadius() > 0
                            && center.x+r > -1.0f
                            && center.x-r < 1.0f
                            && center.y+r > -1.0f
                            && center.y-r < 1.0f) {*/
                        node.render(gl, node.getModelView(), projection, (int)(levelOfDetailScale/r/node.getRadius()));
                    //}
                }
                renderGroup.get(0).unbind(gl);
            }
        }
    }

    /**
     * Reshapes the OpenGL viewport.
     *
     * @param gl     OpenGL
     * @param x      X position
     * @param y      Y position
     * @param width  Width of viewport
     * @param height Height of viewport
     */
    public void reshape(GL3 gl, int x, int y, int width, int height) {
        gl.glViewport(0, 0, width, height);
        setProjection(fieldOfView, width, height, nearPlane, farPlane);
    }
}
