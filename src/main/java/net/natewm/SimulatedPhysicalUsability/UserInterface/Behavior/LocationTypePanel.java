package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private LocationType locationType;

    public LocationTypePanel(LocationType locationType) {
        super();

        this.locationType = locationType;

        setLayout(new GridBagLayout());

        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.ipadx = 2;
        bagConstraints.ipady = 2;

        txtName = new JTextField(locationType.getName());
        txtName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                locationType.setName(txtName.getText());
            }
        });

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
        // TODO: Remove location type when X clicked
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

        // TODO: Transitions
        //transitionPanelList.add(new TransitionPanel());
        //transitionPanelList.add(new TransitionPanel());
        //transitionPanelList.add(new TransitionPanel());

        for (LocationType.Transition transition : locationType.getTransitions()) {
            transitionPanelList.add(new TransitionPanel(transition));
        }

        for (TransitionPanel panel : transitionPanelList) {
            pnlTransitions.add(panel);
        }

        add(pnlTransitions, bagConstraints);

        // TODO: Add Transition Button

        bagConstraints.gridy = 2;
        add(Box.createVerticalStrut(25), bagConstraints);
    }

    public String getLocationName() {
        return txtName.getText();
    }

    public LocationType getLocationType() {
        //return new LocationType(txtName.getText(), (float)spnMinWait.getValue(), (float)spnMaxWait.getValue(), chkOccupied.isSelected());
        // TODO: Additional properties
        //return new LocationType(txtName.getText(), 0f, 1f, false);
        return locationType;
    }

    public void addTransitions(Map<String, LocationType> locationTypeMap) {
        LocationType locationType = locationTypeMap.get(txtName.getText());

        for (TransitionPanel transitionPanel : transitionPanelList) {
            locationType.addTransition(transitionPanel.getTransition(locationTypeMap));
        }
    }
}
