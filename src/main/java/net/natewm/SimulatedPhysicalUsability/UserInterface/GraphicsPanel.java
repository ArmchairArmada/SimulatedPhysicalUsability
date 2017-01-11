package net.natewm.SimulatedPhysicalUsability.UserInterface;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.GraphicsEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 12/22/2016.
 */

// TODO: Clean up this mess
public class GraphicsPanel extends GLCanvas {
    private final GraphicsEngine graphicsEngine;
    private GL3 gl;
    private Animator animator;

    private MouseCamera mouseCamera;

    public GraphicsPanel(GraphicsEngine graphicsEngine, GLCapabilities glCapabilities) {
        super(glCapabilities);

        this.graphicsEngine = graphicsEngine;

        setPreferredSize(new Dimension(1000, 600));

        mouseCamera = new MouseCamera(-0.8f, 0f, 20f, this);

        // TODO: Create camera controls
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

        animator = new Animator(this);
        animator.start();
    }
}
