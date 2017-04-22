package net.natewm.SimulatedPhysicalUsability.UserInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.Project.EnvironmentDescription;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior.BehaviorControlPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior.BehaviorPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Environment.EnvironmentControlPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Environment.EnvironmentPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Simulation.GraphicsPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Simulation.SimulationControlPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * Created by Nathan on 12/21/2016.
 */
public class MainWindow extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    final JFileChooser fileChooser;
    final JFrame window = this;

    public MainWindow(GraphicsEngine graphicsEngine, SimulationThread simulationThread, Environment environment, ProjectData projectData) {
        fileChooser = new JFileChooser();
        ObjectMapper mapper = new ObjectMapper();

        setTitle("Simulated Physical Usability");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

        SimulationControlPanel simulationControls = new SimulationControlPanel(simulationThread, environment);
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

        EnvironmentPanel environmentPanel = new EnvironmentPanel(environment, projectData);
        EnvironmentControlPanel environmentControlPanel = new EnvironmentControlPanel(projectData, environmentPanel);
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

        BehaviorPanel behaviorPanel = new BehaviorPanel(environment, projectData, environmentControlPanel);
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

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //System.out.println("Tab change: " + tabbedPane.getSelectedIndex());
                switch (tabbedPane.getSelectedIndex()) {
                    case 0:     // Simulation
                        break;

                    case 1:     // Environment
                        environmentControlPanel.addButtons();
                        break;

                    case 2:     // Behavior
                        behaviorPanel.populateLocationTypes();
                        break;

                    case 3:     // Data
                        break;

                    default:
                        break;
                }
            }
        });

        add(tabbedPane);

        JMenuBar menuBar = new JMenuBar();

        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("Menu for manipulating and managing files.");
        menuBar.add(menu);

        menuItem = new JMenuItem("New", KeyEvent.VK_N);
        menuItem.getAccessibleContext().setAccessibleDescription("Creates a new blank simulation.");
        menuItem.addActionListener(e -> {
            int dialogResult = JOptionPane.showConfirmDialog (null,
                    "New project? (Unsaved data will be lost)","Warning", JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION) {
                simulationControls.reset();
                simulationThread.stopSimulation();
                environment.clear();
                environmentPanel.clearAll();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Open", KeyEvent.VK_O);
        menuItem.getAccessibleContext().setAccessibleDescription("Opens a file.");
        menuItem.addActionListener(e -> {
            // TODO: File Open
            int returnValue = fileChooser.showOpenDialog(window);
            if (returnValue != JFileChooser.CANCEL_OPTION) {
                File file = fileChooser.getSelectedFile();
                LOGGER.fine("Opening file: " + file.getName());
                try {
                    simulationControls.reset();
                    simulationThread.stopSimulation();
                    environment.clear();
                    projectData.loadProject(file);
                    environmentPanel.updateEnvironment();
                    environment.generateEnvironment();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Save", KeyEvent.VK_S);
        menuItem.getAccessibleContext().setAccessibleDescription("Saves a file.");
        menuItem.addActionListener(e -> {
            // TODO: File Save
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                LOGGER.fine("Saving file: " + file.getName());
                try {
                    projectData.saveProject(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Save As", KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription("Saves a file as.");
        menuItem.addActionListener(e -> {
            // TODO: File Save As
            int returnValue = fileChooser.showSaveDialog(window);
            if (returnValue != JFileChooser.CANCEL_OPTION) {
                File file = fileChooser.getSelectedFile();
                LOGGER.fine("Saving file as: " + file.getName());
                try {
                    projectData.saveProject(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.getAccessibleContext().setAccessibleDescription("Exists the application.");
        menuItem.addActionListener(e -> {
            // TODO: Ask if sure (or ask to save, if not saved)
            System.exit(NORMAL);
        });
        menu.add(menuItem);

        setJMenuBar(menuBar);

        pack();
        setVisible(true);

        simulationThread.start();
    }
}
