package net.natewm.SimulatedPhysicalUsability;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.natewm.SimulatedPhysicalUsability.Graphics.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.Graphics.IAction;
import net.natewm.SimulatedPhysicalUsability.UserInterface.GraphicsPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
                Main main = new Main();
            }
        });
    }
}
