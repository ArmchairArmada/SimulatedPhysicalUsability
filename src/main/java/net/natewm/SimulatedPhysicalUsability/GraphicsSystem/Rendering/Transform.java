package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Encapsulates node transformations.
 */
public class Transform {
    public static final Vector3f right = new Vector3f(1, 0, 0);
    public static final Vector3f up = new Vector3f(0, 1, 0);
    public static final Vector3f forward = new Vector3f(0, 0, 1);

    public Vector3f position = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    private final Matrix4f matrix = new Matrix4f();
    private boolean dirty = true;

    /**
     * Gets matrix, which is the result of position and rotation.
     *
     * @return Matrix
     */
    public Matrix4f getMatrix() {
        if (dirty) {
            updateMatrix();
        }
        return matrix;
    }

    /**
     * Updates the matrix with the current values.
     */
    public void updateMatrix() {
        matrix.identity().translateLocal(position).rotate(rotation);
        dirty = false;
    }

    /**
     * Sets the transform's position and rotation properties.
     *
     * @param transform Transform to use
     */
    public void set(Transform transform) {
        position = transform.position;
        rotation = transform.rotation;
        dirty = true;
    }
}
