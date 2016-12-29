package net.natewm.SimulatedPhysicalUsability.Graphics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Created by Nathan on 12/29/2016.
 */
public class Transform {
    public Vector3f position = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    Matrix4f matrix = new Matrix4f();

    public Matrix4f getMatrix() {
        return matrix;
    }

    public void updateMatrix() {
        matrix.identity().translateLocal(position).rotate(rotation);
    }

    public void setPosition(Vector3f newPosition) {
        position = newPosition;
    }

    public void move(Vector3f offset) {
        position.add(offset);
    }

    public void setRotation(Quaternionf newRotation) {
        rotation = newRotation;
    }

    public void rotate(Quaternionf rotate) {
        rotation.mul(rotate);
    }
}