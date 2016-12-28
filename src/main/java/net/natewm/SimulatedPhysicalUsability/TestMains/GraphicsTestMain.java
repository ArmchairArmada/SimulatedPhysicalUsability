package net.natewm.SimulatedPhysicalUsability.TestMains;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.natewm.SimulatedPhysicalUsability.Graphics.GraphicsPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Nathan on 12/21/2016.
 */
public class GraphicsTestMain extends JFrame {
    public GraphicsTestMain() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });
        setTitle("Simulated Physical Usability");

        GLProfile glProfile = GLProfile.get(GLProfile.GL3);//GLProfile.getDefault();
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        GraphicsPanel graphicsPanel = new GraphicsPanel(glCapabilities);
        add(graphicsPanel);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GraphicsTestMain main = new GraphicsTestMain();
            }
        });
    }
}