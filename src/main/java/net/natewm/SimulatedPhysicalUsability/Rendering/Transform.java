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
    Matrix4f matrix = new Matrix4f();

    /**
     * Gets matrix, which is the result of position and rotation.
     *
     * @return Matrix
     */
    public Matrix4f getMatrix() {
        return matrix;
    }

    /**
     * Updates the matrix with the current values.
     */
    public void updateMatrix() {
        matrix.identity().translateLocal(position).rotate(rotation);
    }
}
