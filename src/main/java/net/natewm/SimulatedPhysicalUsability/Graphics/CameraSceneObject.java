package net.natewm.SimulatedPhysicalUsability.Graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by Nathan on 1/6/2017.
 */
public class CameraSceneObject extends SceneObject {
    float fieldOfView = 49.0f;
    float nearPlane = 0.1f;
    float farPlane = 256.0f;
    float width = 1000;
    float height = 700;

    Matrix4f cameraMatrix = new Matrix4f();
    Matrix4f projectionMatrix = new Matrix4f();

    public CameraSceneObject() {
        super(0f);
    }

    public Matrix4f getCameraMatrix() {
        return cameraMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setCameraProperties(float fov, float near, float far) {
        fieldOfView = fov;
        nearPlane = near;
        farPlane = far;
        updateProjectionMatrix();
    }

    public void resize(float width, float height) {
        this.width = width;
        this.height = height;
        updateProjectionMatrix();
    }

    private void updateProjectionMatrix() {
        projectionMatrix.setPerspective((float)Math.toRadians(fieldOfView), width / height, nearPlane, farPlane);
    }

    public void lookAt(Vector3f position) {
        cameraMatrix.setLookAt(
                transform.position.x, transform.position.y, transform.position.z,
                position.x, position.y, position.z,
                0, 1, 0
        );
    }

    public float getFarPlane() {
        return farPlane;
    }
}
