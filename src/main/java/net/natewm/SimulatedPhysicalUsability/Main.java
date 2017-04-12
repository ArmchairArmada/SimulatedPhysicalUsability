package net.natewm.SimulatedPhysicalUsability;

import net.natewm.SimulatedPhysicalUsability.Environment.*;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.Messaging.Message;
import net.natewm.SimulatedPhysicalUsability.Messaging.Publisher;
import net.natewm.SimulatedPhysicalUsability.Messaging.Subscriber;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;
import net.natewm.SimulatedPhysicalUsability.UserInterface.MainWindow;
import net.natewm.SimulatedPhysicalUsability.Utils.ProbabilityChooser;
import org.joml.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.util.logging.*;

/**
 * Created by Nathan on 12/21/2016.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger("net.natewm.SimulatedPhysicalUsability");

    private static final int GROUND_WIDTH = 8;
    private static final int GROUND_HEIGHT = 8;
    private static final int GROUND_GRID_WIDTH = 128;
    private static final int GROUND_GRID_HEIGHT = 128;

    public Main() {
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

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Insets insets = (Insets) UIManager.getDefaults().get("TabbedPane.contentBorderInsets");
                insets.top = 0;
                UIManager.getDefaults().put("TabbedPane.contentBorderInsets", insets);
                //UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            GraphicsEngine graphicsEngine = new GraphicsEngine();
            graphicsEngine.setRendererClearColor(new float[]{1f,1f,1f,1f});

            Environment environment = new Environment(graphicsEngine);

            SimulationThread simulationThread = new SimulationThread(graphicsEngine, environment);
            graphicsEngine.setFrameReceiver(simulationThread.getFrameEndReciever());

            MainWindow mainWindow = new MainWindow(graphicsEngine, simulationThread, environment);
        });
    }
}
