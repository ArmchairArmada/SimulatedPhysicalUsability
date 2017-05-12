package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

/**
 * Created by Nathan on 12/29/2016.
 */
public class MeshRenderNode implements IRenderNode {
    private final Transform transform = new Transform();
    private Mesh mesh;
    private Material material;

    private final Matrix4f modelView = new Matrix4f();
    private final Matrix4f mvp = new Matrix4f();
    private final Vector4f viewCenter = new Vector4f();
    private float viewRadius=0f;
    private float viewZ=0f;
    private boolean dynamic = false;

    public MeshRenderNode(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    public void init(GL3 gl) {
        mesh.bind(gl);
        material.initAttributes(gl, mesh);
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
        material.bind(gl);
    }

    public void unbind(GL3 gl) {
        mesh.unbind(gl);
        material.unbind(gl);
    }

    public void render(GL3 gl, Matrix4f modelView, Matrix4f projection, int levelOfDetail) {
        //mesh.easyRender(gl,modelView, projection);
        mesh.bindMatrices(gl, material.getModelViewLocation(), material.getProjectionLocation(), modelView, projection);
        mesh.render(gl, levelOfDetail);
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

        // This was a guess based on an estimated focal verticesLength
        // TODO: Figure out how to bind camera's real focal verticesLength
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

    public void updateByteBufferTexture(GL3 gl, int number, ByteBuffer byteBuffer) {
        material.textures.get(number).updateByteBuffer(gl, byteBuffer);
    }

    public void setTexture(GL3 gl, Texture texture, int number) {
        material.textures.set(number, texture);
    }

    public void makeUniqueMaterial() {
        material = new Material(material);
    }
}
