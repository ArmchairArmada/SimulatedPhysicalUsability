package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import javax.swing.*;

/**
 * Created by Nathan on 1/18/2017.
 */
public class BehaviorControlPanel extends JPanel {

    public BehaviorControlPanel(BehaviorPanel behaviorPanel) {
        JButton addLocationButton;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addLocationButton = new JButton("Add Location Type");
        addLocationButton.setAlignmentX(CENTER_ALIGNMENT);
        addLocationButton.addActionListener(e -> behaviorPanel.addLocationType());
        add(addLocationButton);
    }
}
