package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;

import java.util.ArrayList;

/**
 * Created by Nathan on 12/29/2016.
 */
public class Scene {
    ArrayList<SceneNode> nodes = new ArrayList<>();
    ArrayList<SceneNode> toRemove = new ArrayList<>();
    Matrix4f projection = new Matrix4f();

    public Scene() {
    }

    public void setProjection(float fieldOfView, float width, float height, float near, float far) {
        projection.setPerspective((float)Math.toRadians(fieldOfView), width / height, near, far);
    }

    public void add(SceneNode node) {
        nodes.add(node);
    }

    public void remove(SceneNode node) {
        toRemove.add(node);
    }

    public void render(GL3 gl, Matrix4f camera) {
        Matrix4f modelView = new Matrix4f();

        for (SceneNode node : toRemove) {
            nodes.remove(node);
        }
        toRemove.clear();

        for (SceneNode node : nodes) {
            modelView.identity().mul(camera).mul(node.transform.getMatrix());
            node.render(gl, modelView, projection);
        }
    }
}
