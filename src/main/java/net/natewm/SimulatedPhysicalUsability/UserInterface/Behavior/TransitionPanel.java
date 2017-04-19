package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 4/18/2017.
 */
public class TransitionPanel extends JPanel {
    private static final String[] selectionStrings = {"Nearest", "Random"};
    private static final String[] unavailableStrings = {"Repick", "Wait", "Queue"};

    private JTextField txtDestination;
    private JSpinner spnWeight;
    private JComboBox<String> cmbSelection;
    private JComboBox<String> cmbUnavailable;

    public TransitionPanel() {
        super();

        setLayout(new GridBagLayout());

        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.ipadx = 2;
        bagConstraints.ipady = 2;

        bagConstraints.fill = GridBagConstraints.VERTICAL;
        bagConstraints.gridx = 0;
        bagConstraints.weightx = 0;
        add(Box.createHorizontalStrut(50));

        txtDestination = new JTextField("Name of the Destination");

        bagConstraints.fill = GridBagConstraints.BOTH;
        bagConstraints.gridx = 1;
        bagConstraints.weightx = 1;

        add(txtDestination, bagConstraints);

        spnWeight = new JSpinner();

        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.VERTICAL;
        bagConstraints.gridx = 2;

        add(spnWeight, bagConstraints);

        //jSpinner = new JSpinner();
        cmbSelection = new JComboBox<String>(selectionStrings);

        bagConstraints.gridx = 3;

        add(cmbSelection, bagConstraints);

        cmbUnavailable = new JComboBox<String>(unavailableStrings);

        bagConstraints.gridx = 4;

        add(cmbUnavailable, bagConstraints);

        JButton closeButton = new JButton("X");
        closeButton.setMargin(new Insets(5, 5, 5, 5));

        bagConstraints.gridx = 5;

        add(closeButton, bagConstraints);
    }
}
