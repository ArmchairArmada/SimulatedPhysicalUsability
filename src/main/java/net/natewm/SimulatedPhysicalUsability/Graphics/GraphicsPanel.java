package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import org.joml.Vector3f;

import java.awt.*;

/**
 * Created by Nathan on 12/22/2016.
 */
public class GraphicsPanel extends GLCanvas {
    private GL3 gl;
    private FPSAnimator animator;

    private float x = 0.0f;
    private float dx = 0.01f;

    public GraphicsPanel(GLCapabilities glCapabilities) {
        super(glCapabilities);

        setPreferredSize(new Dimension(1000, 600));

        addGLEventListener(new GLEventListener() {
            Mesh mesh;

            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                Geometry geometry = new Geometry();
                geometry.addVertex(new Vector3f(-0.5f, 0.5f, 0.0f));
                geometry.addVertex(new Vector3f(-0.5f, -0.5f, 0.0f));
                geometry.addVertex(new Vector3f(0.5f, -0.5f, 0.0f));
                geometry.addVertex(new Vector3f(0.5f, 0.5f, 0.0f));

                geometry.addColor(new Vector3f(1.0f, 0.0f, 0.0f));
                geometry.addColor(new Vector3f(0.0f, 1.0f, 0.0f));
                geometry.addColor(new Vector3f(0.0f, 0.0f, 1.0f));
                geometry.addColor(new Vector3f(1.0f, 1.0f, 1.0f));

                geometry.addTriangle(new Triangle(0, 1, 2));
                geometry.addTriangle(new Triangle(0, 2, 3));

                try {
                    mesh = new Mesh(gl, geometry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void dispose(GLAutoDrawable glAutoDrawable) {
                mesh.dispose(gl);
            }

            public void display(GLAutoDrawable glAutoDrawable) {
                x += dx;
                if (x < 0.0f || x > 1.0f) {
                    x -= dx;
                    dx = -dx;
                }

                gl.glClearColor(x, 0.0f, 0.0f, 1.0f);
                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

                mesh.easyRender(gl);
            }

            public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
                gl.glViewport(0, 0, width, height);
            }
        });

        animator = new FPSAnimator(this, 60);
        animator.start();
    }
}
