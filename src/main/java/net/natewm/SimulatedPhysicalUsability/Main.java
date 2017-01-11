package net.natewm.SimulatedPhysicalUsability;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.IFrameEndReciever;
import net.natewm.SimulatedPhysicalUsability.Simulation.AgentManager;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;
import net.natewm.SimulatedPhysicalUsability.UserInterface.GraphicsPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.*;

/**
 * Created by Nathan on 12/21/2016.
 */
public class Main extends JFrame {
    private static final Logger LOGGER = Logger.getLogger("net.natewm.SimulatedPhysicalUsability");

    public Main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Simulated Physical Usability");

        GLProfile glProfile = GLProfile.get(GLProfile.GL3);//GLProfile.getDefault();
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        GraphicsEngine graphicsEngine = new GraphicsEngine();
        SimulationThread simulationThread = new SimulationThread(graphicsEngine);
        graphicsEngine.setFrameReciever(simulationThread.getFrameEndReciever());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                simulationThread.dispose();

                super.windowClosing(e);
            }
        });

        GraphicsPanel graphicsPanel = new GraphicsPanel(graphicsEngine, glCapabilities);
        add(graphicsPanel);

        pack();
        setVisible(true);

        simulationThread.start();
    }

    public static void main(String[] args) {
        final String format = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS - %4$s - %2$-90s - %5$s%6$s%n";
        final String key = "java.util.logging.SimpleFormatter.format";
        System.setProperty(key, format);

        Formatter formatter = new SimpleFormatter();
        Handler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);

        LOGGER.log(Level.FINE, "Starting application.");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main main = new Main();
            }
        });
    }
}
