package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;

/**
 * Created by Nathan on 12/29/2016.
 */
public class SceneNode {
    Transform transform = new Transform();
    Mesh mesh;

    public SceneNode(Mesh mesh) {
        this.mesh = mesh;
    }

    public Transform getTransform() {
        return transform;
    }

    public void render(GL3 gl, Matrix4f modelView, Matrix4f projection) {
        mesh.easyRender(gl,modelView, projection);
        //mesh.render(gl);
    }
}
