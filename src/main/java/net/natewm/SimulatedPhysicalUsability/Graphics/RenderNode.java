package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Created by Nathan on 12/29/2016.
 */
public class RenderNode {
    Transform transform = new Transform();
    Mesh mesh;

    Matrix4f modelView = new Matrix4f();
    Matrix4f mvp = new Matrix4f();
    Vector4f viewCenter = new Vector4f();
    float viewRadius=0f, viewZ=0f;

    public RenderNode(Mesh mesh) {
        this.mesh = mesh;
    }

    public Transform getTransform() {
        return transform;
    }

    public void bind(GL3 gl) {
        mesh.bind(gl);
    }

    public void unbind(GL3 gl) {
        mesh.unbind(gl);
    }

    public void render(GL3 gl, Matrix4f modelView, Matrix4f projection) {
        //mesh.easyRender(gl,modelView, projection);
        mesh.bindMatricies(gl, modelView, projection);
        mesh.render(gl);
    }

    public float getRadius() {
        return mesh.radius;
    }

    void updateView(Matrix4f camera, Matrix4f projection) {
        modelView.set(camera).mul(transform.getMatrix());
        mvp.set(projection).mul(modelView);
        viewCenter.set(0f,0f,0f,1f).mul(mvp);

        viewZ = viewCenter.z;

        viewCenter.mul(1/viewCenter.w);

        // This was a guess based on an estimated focal length
        // TODO: Figure out how to use camera's real focal length
        viewRadius = 4f*mesh.radius / viewZ;
    }
}
