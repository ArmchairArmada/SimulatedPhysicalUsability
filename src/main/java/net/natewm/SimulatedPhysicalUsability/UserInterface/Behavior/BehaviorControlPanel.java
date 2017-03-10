package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import net.natewm.SimulatedPhysicalUsability.UserInterface.Environment.EnvironmentPanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nathan on 1/18/2017.
 */
public class BehaviorControlPanel extends JPanel {
     BehaviorPanel behaviorPanel;

    public BehaviorControlPanel(BehaviorPanel behaviorPanel) {
        JButton addLocationButton;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addLocationButton = new JButton("Add Location Type");
        addLocationButton.setAlignmentX(CENTER_ALIGNMENT);
        add(addLocationButton);
    }
}
