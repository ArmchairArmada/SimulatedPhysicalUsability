package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathan on 12/29/2016.
 */

/**
 * Renders an OpenGL scene.
 */
public class Renderer {
    private ArrayList<IRenderNode> dynamicNodes = new ArrayList<>();
    private ArrayList<ArrayList<IRenderNode>> renderGroups = new ArrayList<>();
    private Map<Integer, Integer> renderIndexMap = new HashMap<>();
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
            renderGroups.get(node.getGroupID()).remove(node);
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

        // This is being done in parallel, to speed up matrix multiplicaitons
        dynamicNodes.parallelStream().forEach((node) -> {
            //node.getTransform().updateMatrix();
            node.updateView(camera, projection);
        });

        for (IRenderNode node : dynamicNodes) {
            // Two loops are used, since I could not get locking to work to prevent OpenGL conflicts.
            center = node.getViewCenter();
            r = node.getViewRadius();
            if (node.getViewZ()-node.getRadius() < farPlane
                    && node.getViewZ()+node.getRadius() > 0
                    && center.x+r > -1.0f
                    && center.x-r < 1.0f
                    && center.y+r > -1.0f
                    && center.y-r < 1.0f) {
                node.bind(gl);
                node.render(gl, node.getModelView(), projection);
                node.unbind(gl);
            }
        }

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
                    if (node.getViewZ()-node.getRadius() < farPlane
                            && node.getViewZ()+node.getRadius() > 0
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
