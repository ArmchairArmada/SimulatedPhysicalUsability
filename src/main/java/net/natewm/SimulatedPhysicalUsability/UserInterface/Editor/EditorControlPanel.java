package net.natewm.SimulatedPhysicalUsability.UserInterface.Editor;

import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nathan on 1/18/2017.
 */
public class EditorControlPanel extends JPanel {
    EditorPanel editorPanel;

    public EditorControlPanel(EditorPanel editorPanel) {
        JButton button;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        button = new JButton("Clear All");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorPanel.clearAll();
            }
        });
        add(button);

        button = new JButton("Eraser");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorPanel.setTool(EditorPanel.Tool.ERASER);
            }
        });
        add(button);

        button = new JButton("Add Walls");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorPanel.setTool(EditorPanel.Tool.WALLS);
            }
        });
        add(button);

        button = new JButton("Add Locations");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorPanel.setTool(EditorPanel.Tool.LOCATION);
            }
        });
        add(button);

        // TODO: Add list of locations.
    }
}
