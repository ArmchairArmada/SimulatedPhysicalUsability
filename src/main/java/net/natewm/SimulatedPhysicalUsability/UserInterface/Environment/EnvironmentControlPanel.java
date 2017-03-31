package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nathan on 1/18/2017.
 */
public class EnvironmentControlPanel extends JPanel {
    EnvironmentPanel environmentPanel;

    public EnvironmentControlPanel(EnvironmentPanel environmentPanel) {
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

        // TODO: Add list of locations.
    }
}
