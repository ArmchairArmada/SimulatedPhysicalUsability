package net.natewm.SimulatedPhysicalUsability;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.IFrameEndReciever;
import net.natewm.SimulatedPhysicalUsability.Resources.ResourceManager;
import net.natewm.SimulatedPhysicalUsability.Simulation.AgentManager;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;
import net.natewm.SimulatedPhysicalUsability.UserInterface.GraphicsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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


        // MOCK USER INTERFACE LAYOUT
        // TODO: Split UI components into separate classes
        JMenuBar menuBar = new JMenuBar();

        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("Menu for manipulating and managing files.");
        menuBar.add(menu);

        menuItem = new JMenuItem("New", KeyEvent.VK_N);
        menuItem.getAccessibleContext().setAccessibleDescription("Creates a new blank simulation.");
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.getAccessibleContext().setAccessibleDescription("Exists the application.");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Ask if sure (or ask to save, if not saved)
                System.exit(NORMAL);
            }
        });
        menu.add(menuItem);

        setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel simulationTabPanel = new JPanel();

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        simulationTabPanel.setLayout(layout);

        JPanel simulationControls = new JPanel();
        simulationControls.setMinimumSize(new Dimension(250, 0));
        simulationControls.setMaximumSize(new Dimension(250, 1000000));
        simulationControls.setPreferredSize(new Dimension(250, 600));

        simulationTabPanel.add(simulationControls, gridBagConstraints);


        // DONE MOCK USER INTERFACE

        GLProfile glProfile = GLProfile.get(GLProfile.GL3);//GLProfile.getDefault();
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        GraphicsEngine graphicsEngine = new GraphicsEngine();

        ResourceManager resourceManager = new ResourceManager(graphicsEngine);

        SimulationThread simulationThread = new SimulationThread(graphicsEngine, resourceManager);
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
        graphicsPanel.setPreferredSize(new Dimension(800, 600));

        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1000000;

        simulationTabPanel.add(graphicsPanel, gridBagConstraints);

        tabbedPane.addTab("Simulation", simulationTabPanel);

        // TODO: Make real tabs -- remember to suspend limit rendering to 60 FPS
        tabbedPane.addTab("Environment", new JPanel());
        tabbedPane.addTab("Behaviors", new JPanel());
        tabbedPane.addTab("Statistics", new JPanel());

        add(tabbedPane);

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
