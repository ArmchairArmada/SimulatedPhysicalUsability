package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;

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
    ArrayList<JButton> locationTypeButtonList = new ArrayList<>();

    public EnvironmentControlPanel(ProjectData projectData, EnvironmentPanel environmentPanel) {
        this.environmentPanel = environmentPanel;
        this.projectData = projectData;
        buildUI();
    }

    private void buildUI() {
        removeAll();

        JButton button;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        button = new JButton("Clear All");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> environmentPanel.clearAll());
        add(button);

        button = new JButton("Eraser");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> environmentPanel.setTool(EnvironmentPanel.Tool.ERASER));
        add(button);

        button = new JButton("Add Walls");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> environmentPanel.setTool(EnvironmentPanel.Tool.WALLS));
        add(button);

        button = new JButton("Add Locations");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> environmentPanel.setTool(EnvironmentPanel.Tool.LOCATION));
        add(button);

        button = new JButton("Apply Changes");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> environmentPanel.applyChanges());
        add(button);

        add(Box.createVerticalStrut(10));

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setAlignmentX(CENTER_ALIGNMENT);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        // TODO: Move locations into scrollpane
        for (JButton btn : locationTypeButtonList) {
            jPanel.add(btn);
        }

        add(jScrollPane);
    }

    public void addButtons() {
        JButton button;
        locationTypeButtonList.clear();

        for (LocationType locationType : projectData.getLocationTypes()) {
            button = new JButton(locationType.getName());
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
