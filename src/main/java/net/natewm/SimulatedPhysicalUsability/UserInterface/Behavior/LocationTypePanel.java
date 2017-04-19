package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nathan on 4/18/2017.
 */
public class LocationTypePanel extends JPanel {
    private JTextField txtName;
    private JCheckBox chkExit;
    private JCheckBox chkOccupied;
    private JSpinner spnMinWait;
    private JSpinner spnMaxWait;
    private ColorButton btnColor;
    private ArrayList<TransitionPanel> transitionPanelList = new ArrayList<>();

    public LocationTypePanel() {
        super();

        setLayout(new GridBagLayout());

        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.ipadx = 2;
        bagConstraints.ipady = 2;

        txtName = new JTextField("Name of the Location");

        bagConstraints.fill = GridBagConstraints.BOTH;
        bagConstraints.gridx = 0;
        bagConstraints.weightx = 1;

        add(txtName, bagConstraints);

        btnColor = new ColorButton();

        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.VERTICAL;
        bagConstraints.gridx = 1;

        add(btnColor, bagConstraints);

        //jSpinner = new JSpinner();

        bagConstraints.gridx = 2;

        //testLocation.add(jSpinner, bagConstraints);

        JButton closeButton = new JButton("X");
        closeButton.setMargin(new Insets(5, 5, 5, 5));

        bagConstraints.gridx = 3;

        add(closeButton, bagConstraints);

        bagConstraints.gridx = 0;
        bagConstraints.gridy = 1;
        bagConstraints.gridwidth = 4;
        bagConstraints.weightx = 1;
        bagConstraints.fill = GridBagConstraints.BOTH;

        JPanel pnlTransitions = new JPanel();
        pnlTransitions.setLayout(new BoxLayout(pnlTransitions, BoxLayout.Y_AXIS));

        transitionPanelList.add(new TransitionPanel());
        transitionPanelList.add(new TransitionPanel());
        transitionPanelList.add(new TransitionPanel());

        for (TransitionPanel panel : transitionPanelList) {
            pnlTransitions.add(panel);
        }

        add(pnlTransitions, bagConstraints);

        // TODO: Add Transition Button

        bagConstraints.gridy = 2;
        add(Box.createVerticalStrut(25), bagConstraints);
    }
}
