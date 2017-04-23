package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Border;
import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nathan on 1/18/2017.
 */
public class EnvironmentControlPanel extends JPanel {
    EnvironmentPanel environmentPanel;
    final ProjectData projectData;
    final SimulationThread simulationThread;
    ArrayList<JToggleButton> locationTypeButtonList = new ArrayList<>();
    ButtonGroup tglButtons;

    public EnvironmentControlPanel(ProjectData projectData, EnvironmentPanel environmentPanel, SimulationThread simulationThread) {
        this.environmentPanel = environmentPanel;
        this.projectData = projectData;
        this.simulationThread = simulationThread;
        buildUI();
    }

    private void buildUI() {
        removeAll();
        tglButtons = new ButtonGroup();

        JButton button;
        JToggleButton tglButton;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        button = new JButton("Clear All");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> environmentPanel.clearAll());
        add(button);

        add(Box.createVerticalStrut(10));

        button = new JButton("Apply Changes");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> {
            simulationThread.stopSimulation();
            environmentPanel.applyChanges();
        });
        add(button);

        add(Box.createVerticalStrut(10));

        tglButton = new JToggleButton("Eraser");
        tglButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        tglButton.addActionListener(e -> {
            environmentPanel.setTool(EnvironmentPanel.Tool.ERASER);
        });
        tglButtons.add(tglButton);
        add(tglButton);

        tglButton = new JToggleButton("Add Walls");
        tglButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        tglButton.addActionListener(e -> environmentPanel.setTool(EnvironmentPanel.Tool.WALLS));
        tglButtons.add(tglButton);
        add(tglButton);

        add(Box.createVerticalStrut(10));

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane.setAlignmentX(CENTER_ALIGNMENT);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        JLabel lblLoc = new JLabel("Locations");
        lblLoc.setAlignmentX(CENTER_ALIGNMENT);
        add(lblLoc);

        // TODO: Move locations into scrollpane
        for (JToggleButton btn : locationTypeButtonList) {
            jPanel.add(btn);
            tglButtons.add(btn);
        }

        add(jScrollPane);
    }

    public void addButtons() {
        JToggleButton button;
        locationTypeButtonList.clear();

        for (LocationType locationType : projectData.getLocationTypes()) {
            button = new JToggleButton(locationType.getName());
            button.setAlignmentX(CENTER_ALIGNMENT);
            // TODO: Set the location type tool.
            button.addActionListener(e -> {
                environmentPanel.setLocationTool(locationType);
            });
            locationTypeButtonList.add(button);
        }

        buildUI();
    }
}
