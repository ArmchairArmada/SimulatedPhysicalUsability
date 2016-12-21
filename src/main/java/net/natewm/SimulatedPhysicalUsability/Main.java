package net.natewm.SimulatedPhysicalUsability;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 12/21/2016.
 */
public class Main extends JFrame {
    public Main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Simulated Physical Usability");

        GLProfile glProfile = GLProfile.getDefault();
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        GLCanvas glCanvas = new GLCanvas(glCapabilities);

        glCanvas.setPreferredSize(new Dimension(800, 600));

        glCanvas.addGLEventListener(new GLEventListener() {
            public void init(GLAutoDrawable glAutoDrawable) {

            }

            public void dispose(GLAutoDrawable glAutoDrawable) {

            }

            public void display(GLAutoDrawable glAutoDrawable) {
                GL3 gl3 = glAutoDrawable.getGL().getGL3();
                gl3.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
                gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            }

            public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
                GL3 gl3 = glAutoDrawable.getGL().getGL3();
                GLU glu = new GLU();
                glu.gluOrtho2D(0.0f, width, 0.0f, height);

                gl3.glViewport(0, 0, width, height);
            }
        });

        add(glCanvas);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main main = new Main();
            }
        });
    }
}
