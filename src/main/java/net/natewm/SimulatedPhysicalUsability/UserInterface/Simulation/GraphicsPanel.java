package net.natewm.SimulatedPhysicalUsability.UserInterface.Simulation;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;

/**
 * Created by Nathan on 12/22/2016.
 */

public class GraphicsPanel extends GLCanvas {
    private GL3 gl;
    private FPSAnimator animator;

    private final MouseCamera mouseCamera;

    public GraphicsPanel(GraphicsEngine graphicsEngine) {
        super(graphicsEngine.getGlCapabilities());

        mouseCamera = new MouseCamera(-0.8f, 3.1415f, 20f, this);

        addMouseListener(mouseCamera.getMouseListener());
        addMouseMotionListener(mouseCamera.getMouseMotionListener());
        addMouseWheelListener(mouseCamera.getMouseWheelListener());


        addGLEventListener(new GLEventListener() {
            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                graphicsEngine.init(gl);
                animator.setUpdateFPSFrames(60, System.out);
            }

            public void dispose(GLAutoDrawable glAutoDrawable) {
            }

            public void display(GLAutoDrawable glAutoDrawable) {
                graphicsEngine.setCameraMatrix(mouseCamera.getMatrix());
                graphicsEngine.render(gl);
                graphicsEngine.processActions(gl);
            }

            public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
                graphicsEngine.reshape(x, y, width, height);
            }
        });

        animator = new FPSAnimator(this, 70);
        animator.start();
    }
}
