package net.natewm.SimulatedPhysicalUsability.Rendering;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Created by Nathan on 12/29/2016.
 */

/**
 * Encapsulates node transformations.
 */
public class Transform {
    public static final Vector3f right = new Vector3f(1, 0, 0);
    public static final Vector3f up = new Vector3f(0, 1, 0);
    public static final Vector3f forward = new Vector3f(0, 0, 1);

    public Vector3f position = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    private Matrix4f matrix = new Matrix4f();
    private boolean dirty = true;

    public Transform() {
    }

    public Transform(Transform transform) {
        position = new Vector3f(transform.position);
        rotation = new Quaternionf(transform.rotation);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        dirty = true;
    }

    public void setAxisRotation(float angle, float x, float y, float z) {
        rotation.setAngleAxis(angle, x, y, z);
        dirty = true;
    }

    /**
     * Gets matrix, which is the result of position and rotation.
     *
     * @return Matrix
     */
    public Matrix4f getMatrix() {
        if (dirty) {
            dirty = false;
            updateMatrix();
        }
        return matrix;
    }

    /**
     * Updates the matrix with the current values.
     */
    public void updateMatrix() {
        matrix.identity().translateLocal(position).rotate(rotation);
    }
}
