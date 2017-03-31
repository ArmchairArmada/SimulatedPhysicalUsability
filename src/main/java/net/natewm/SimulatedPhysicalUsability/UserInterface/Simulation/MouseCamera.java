package net.natewm.SimulatedPhysicalUsability.UserInterface.Simulation;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.event.*;

/**
 * Camera control system which uses the mouse for translation, rotation, and zoom.
 */
public class MouseCamera {
    static final float TRANSLATE_RATE = 0.0007f;
    static final float ZOOM_RATE = 0.0025f;
    static final float ROTATE_RATE = 0.001f;
    static final float SCROLL_RATE = 0.05f;
    static final float MIN_ROTATE = (float)(-Math.PI/2.0+0.025f);
    static final float MAX_ROTATE = -0.001f;
    static final float MIN_ZOOM = 2f;

    MouseListener mouseListener;
    MouseMotionListener mouseMotionListener;
    MouseWheelListener mouseWheelListener;

    int button = 0;
    float previousX = 0f;
    float previousY = 0f;

    float rotateX = 0f;
    float rotateY = 0f;
    float distance = 10f;

    Component component;

    Vector3f cameraCenter = new Vector3f();
    Quaternionf cameraAngle = new Quaternionf();

    Matrix4f matrix = new Matrix4f();

    /**
     * Constructor to create the mouse controlled camera.
     *
     * @param cameraRotateX  Initial rotation around X axis
     * @param cameraRotateY  Initial rotation around Y axis
     * @param cameraDistance Initial zoom distance
     */
    public MouseCamera(float cameraRotateX, float cameraRotateY, float cameraDistance, Component component) {
        this.rotateX = cameraRotateX;
        this.rotateY = cameraRotateY;
        this.distance = cameraDistance;
        this.component = component;

        cameraAngle.identity().rotateAxis(rotateY, 0f, 1f, 0f).rotateAxis(rotateX, 1f, 0f, 0f);
        updateMatrix();

        mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button = e.getButton();
                previousX = e.getX();
                previousY = e.getY();

                switch (button) {
                    case 1:
                        component.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        break;

                    case 2:
                        component.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                        break;

                    case 3:
                        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        break;

                    default:
                        component.setCursor(Cursor.getDefaultCursor());
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button = 0;
                component.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

        mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Vector3f v = new Vector3f();
                Quaternionf q = new Quaternionf();

                if (button == 1) {
                    rotateY -= (e.getX() - previousX) * ROTATE_RATE;
                    rotateX += (e.getY() - previousY) * ROTATE_RATE;
                    if (rotateX > MAX_ROTATE)
                        rotateX = MAX_ROTATE;
                    if (rotateX < MIN_ROTATE)
                        rotateX = MIN_ROTATE;
                    cameraAngle.identity().rotateAxis(rotateY, 0f, 1f, 0f).rotateAxis(rotateX, 1f, 0f, 0f);
                }
                if (button == 2) {
                    distance += (e.getY() - previousY) * distance * ZOOM_RATE;

                    if (distance < MIN_ZOOM)
                        distance = MIN_ZOOM;
                }
                if (button == 3) {
                    q.identity().rotateAxis(rotateY, 0f, 1f, 0f);

                    v.set(1f, 0f, 0f);
                    v.rotate(q);
                    v.mul((e.getX() - previousX) * TRANSLATE_RATE * distance);
                    cameraCenter.add(v);

                    v.set(0f, 0f, 1f);
                    v.rotate(q);
                    v.mul((e.getY() - previousY) * TRANSLATE_RATE * distance);
                    cameraCenter.add(v);
                }

                previousX = e.getX();
                previousY = e.getY();

                updateMatrix();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        };

        mouseWheelListener = e -> {
            distance += e.getPreciseWheelRotation() * distance * SCROLL_RATE;

            if (distance < MIN_ZOOM)
                distance = MIN_ZOOM;

            updateMatrix();
        };
    }

    /**
     * Gets the camera matrix.
     *
     * @return Camera matrix.
     */
    public Matrix4f getMatrix() {
        return matrix;
    }

    /**
     * Gets the mouse listener, to be used with Swing
     *
     * @return Mouse listener
     */
    public MouseListener getMouseListener() {
        return mouseListener;
    }

    /**
     * Gets the mouse motion listener, to be used with Swing.
     *
     * @return Mouse motion listener
     */
    public MouseMotionListener getMouseMotionListener() {
        return mouseMotionListener;
    }

    /**
     * Gets mouse wheel listener, to be used with Swing.
     *
     * @return Mouse wheel listener
     */
    public MouseWheelListener getMouseWheelListener() {
        return mouseWheelListener;
    }

    /**
     * Updates the camera's matrix.
     */
    private void updateMatrix() {
        Vector3f v = new Vector3f(0f, distance, 0f);
        v.rotate(cameraAngle);
        v.add(cameraCenter);

        matrix.setLookAt(v.x, v.y, v.z, cameraCenter.x, 0.5f, cameraCenter.z, 0, 1, 0);
    }
}
