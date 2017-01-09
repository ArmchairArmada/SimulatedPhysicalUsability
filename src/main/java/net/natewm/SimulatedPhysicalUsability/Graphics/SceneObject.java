package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Created by Nathan on 1/6/2017.
 */
public class SceneObject {
    Transform transform = new Transform();
    float radius;

    public SceneObject(float radius) {
        this.radius = radius;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void render(GL3 gl, Matrix4f cameraMatrix, Matrix4f projectionMatrix, float farPlane) {
        Matrix4f modelViewMatrix = new Matrix4f(cameraMatrix).mul(transform.getMatrix());
        if (testVisisble(cameraMatrix, projectionMatrix, modelViewMatrix, farPlane)) {
            renderObject(gl, modelViewMatrix, projectionMatrix);
        }
    }

    protected void renderObject(GL3 gl, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        // OVERRIDE THIS FOR EACH OBJECT
    }

    public boolean isReady() {
        return true;
    }

    public boolean testVisisble(Matrix4f cameraMatrix, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, float farPlane) {
        Matrix4f mvp = new Matrix4f();

        modelViewMatrix.set(cameraMatrix).mul(transform.getMatrix());
        mvp.set(projectionMatrix).mul(modelViewMatrix);
        Vector4f viewCenter = new Vector4f(0f,0f,0f,1f).mul(mvp);

        float viewZ = viewCenter.z;

        viewCenter.mul(1/viewCenter.w);
        float x = viewCenter.x;
        float y = viewCenter.y;

        // This was a guess based on an estimated focal length
        // TODO: Figure out how to use camera's real focal length
        float viewRadius;
        if (viewZ > 0f)
            viewRadius = 4f*radius / viewZ;
        else
            viewRadius = Float.POSITIVE_INFINITY;

        return viewZ - radius < farPlane
                && viewZ + radius > 0
                && x + viewRadius > -1.0f
                && x - viewRadius < 1.0f
                && y + viewRadius > -1.0f
                && y - viewRadius < 1.0f;
    }

    // TODO: Add SceneObject to scene (if anything actually needs to be done)
}
