package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Information.FloatGrid;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Created by Nathan on 12/29/2016.
 */
public class MeshRenderNode implements IRenderNode {
    Transform transform = new Transform();
    Mesh mesh;

    Matrix4f modelView = new Matrix4f();
    Matrix4f mvp = new Matrix4f();
    Vector4f viewCenter = new Vector4f();
    float viewRadius=0f, viewZ=0f;
    boolean dynamic = false;

    public MeshRenderNode(Mesh mesh) {
        this.mesh = mesh;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public int getGroupID() {
        return mesh.getVao();
    }

    public void bind(GL3 gl) {
        mesh.bind(gl);
    }

    public void unbind(GL3 gl) {
        mesh.unbind(gl);
    }

    public void render(GL3 gl, Matrix4f modelView, Matrix4f projection) {
        //mesh.easyRender(gl,modelView, projection);
        mesh.bindMatrices(gl, modelView, projection);
        mesh.render(gl);
    }

    public float getRadius() {
        return mesh.radius;
    }

    public void updateView(Matrix4f camera, Matrix4f projection) {
        modelView.set(camera).mul(transform.getMatrix());
        mvp.set(projection).mul(modelView);
        viewCenter.set(0f,0f,0f,1f).mul(mvp);

        viewZ = viewCenter.z;

        viewCenter.mul(1/viewCenter.w);

        // This was a guess based on an estimated focal length
        // TODO: Figure out how to bind camera's real focal length
        if (viewZ > 0f)
            viewRadius = 4f*mesh.radius / viewZ;
        else
            viewRadius = Float.POSITIVE_INFINITY;
    }

    public Matrix4f getModelView() {
        return modelView;
    }

    public Vector4f getViewCenter() {
        return viewCenter;
    }

    public float getViewRadius() {
        return viewRadius;
    }

    public float getViewZ() {
        return viewZ;
    }

    public void updateFloatGridTexture(GL3 gl, FloatGrid floatGrid, int number) {
        mesh.material.textures.get(number).updateFloatGrid(gl, floatGrid);
    }

    public void setTexture(GL3 gl, Texture texture, int number) {
        mesh.material.textures.set(number, texture);
    }
}
