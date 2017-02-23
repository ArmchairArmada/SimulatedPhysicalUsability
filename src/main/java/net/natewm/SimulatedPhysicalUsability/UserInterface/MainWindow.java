package net.natewm.SimulatedPhysicalUsability.UserInterface;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Editor.EditorControlPanel;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Editor.EditorPanel;

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


        JPanel editorTabPanel = new JPanel();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1;
        editorTabPanel.setLayout(layout);

        EditorPanel editorPanel = new EditorPanel();
        EditorControlPanel editorControlPanel = new EditorControlPanel(editorPanel);
        editorControlPanel.setMinimumSize(new Dimension(250, 0));
        editorControlPanel.setMaximumSize(new Dimension(250, 1000000));
        editorControlPanel.setPreferredSize(new Dimension(250, 600));

        editorTabPanel.add(editorControlPanel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1000000;

        editorTabPanel.add(editorPanel, gridBagConstraints);

        tabbedPane.addTab("Environment", editorTabPanel);


        // TODO: Make real tabs -- remember to suspend limit rendering to 60 FPS
        tabbedPane.addTab("Behaviors", new JPanel());
        tabbedPane.addTab("Statistics", new JPanel());

        add(tabbedPane);

        pack();
        setVisible(true);

        simulationThread.start();
    }
}
