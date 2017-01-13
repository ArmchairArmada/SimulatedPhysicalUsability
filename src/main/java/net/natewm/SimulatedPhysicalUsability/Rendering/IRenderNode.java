package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Created by Nathan on 12/31/2016.
 */
public interface IRenderNode {

    public Transform getTransform();

    public void init(GL3 gl);

    public void setDynamic(boolean dynamic);

    public boolean isDynamic();

    public int getGroupID();

    public void bind(GL3 gl);

    public void unbind(GL3 gl);

    public void render(GL3 gl, Matrix4f modelView, Matrix4f projection, int levelOfDetail);

    public float getRadius();

    public void updateView(Matrix4f camera, Matrix4f projection);

    public Matrix4f getModelView();

    public Vector4f getViewCenter();

    public float getViewRadius();

    public float getViewZ();

}
