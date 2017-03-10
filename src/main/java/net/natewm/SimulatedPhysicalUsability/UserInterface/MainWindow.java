package net.natewm.SimulatedPhysicalUsability.UserInterface;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior.BehaviorControlPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior.BehaviorPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Environment.EnvironmentControlPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Environment.EnvironmentPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Simulation.GraphicsPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Simulation.SimulationControlPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;

/**
 * Created by Nathan on 12/21/2016.
 */
public class MainWindow extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    public MainWindow(GraphicsEngine graphicsEngine, SimulationThread simulationThread, GroundGrid groundGrid) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Insets insets = (Insets) UIManager.getDefaults().get("TabbedPane.contentBorderInsets");
            insets.top = 0;
            UIManager.getDefaults().put("TabbedPane.contentBorderInsets", insets);
            //UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Simulated Physical Usability");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                simulationThread.dispose();

                super.windowClosing(e);
            }
        });

        GraphicsPanel graphicsPanel = new GraphicsPanel(graphicsEngine);
        graphicsPanel.setPreferredSize(new Dimension(800, 600));

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

        SimulationControlPanel simulationControls = new SimulationControlPanel(simulationThread, groundGrid);
        simulationControls.setMinimumSize(new Dimension(250, 0));
        simulationControls.setMaximumSize(new Dimension(250, 1000000));
        simulationControls.setPreferredSize(new Dimension(250, 600));

        simulationTabPanel.add(simulationControls, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1000000;

        simulationTabPanel.add(graphicsPanel, gridBagConstraints);

        tabbedPane.addTab("Simulation", simulationTabPanel);


        JPanel environmentTabPanel = new JPanel();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1;
        environmentTabPanel.setLayout(layout);

        EnvironmentPanel environmentPanel = new EnvironmentPanel();
        EnvironmentControlPanel environmentControlPanel = new EnvironmentControlPanel(environmentPanel);
        environmentControlPanel.setMinimumSize(new Dimension(250, 0));
        environmentControlPanel.setMaximumSize(new Dimension(250, 1000000));
        environmentControlPanel.setPreferredSize(new Dimension(250, 600));

        environmentTabPanel.add(environmentControlPanel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1000000;

        environmentTabPanel.add(environmentPanel, gridBagConstraints);

        tabbedPane.addTab("Environment", environmentTabPanel);

        JPanel behaviorTabPanel = new JPanel();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1;
        behaviorTabPanel.setLayout(layout);

        BehaviorPanel behaviorPanel = new BehaviorPanel();
        BehaviorControlPanel behaviorControlPanel = new BehaviorControlPanel(behaviorPanel);
        behaviorControlPanel.setMinimumSize(new Dimension(250, 0));
        behaviorControlPanel.setMaximumSize(new Dimension(250, 1000000));
        behaviorControlPanel.setPreferredSize(new Dimension(250, 600));

        behaviorTabPanel.add(behaviorControlPanel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1000000;

        behaviorTabPanel.add(behaviorPanel, gridBagConstraints);

        tabbedPane.addTab("Behaviors", behaviorTabPanel);


        // TODO: Make real tabs -- remember to suspend limit rendering to 60 FPS
        tabbedPane.addTab("Statistics", new JPanel());

        add(tabbedPane);

        pack();
        setVisible(true);

        simulationThread.start();
    }
}
