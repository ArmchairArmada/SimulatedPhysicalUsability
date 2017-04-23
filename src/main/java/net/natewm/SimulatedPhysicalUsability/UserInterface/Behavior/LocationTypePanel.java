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
    private JCheckBox chkEntrance;
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

        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.VERTICAL;
        bagConstraints.gridx = 1;

        add(new JLabel("Min Wait"), bagConstraints);

        bagConstraints.gridx = 2;
        spnMinWait = new JSpinner();
        spnMinWait.setValue(locationType.getMinWaitTime());
        add(spnMinWait, bagConstraints);

        bagConstraints.gridx = 3;
        add(new JLabel("Max Wait"), bagConstraints);

        bagConstraints.gridx = 4;
        spnMaxWait = new JSpinner();
        spnMaxWait.setValue(locationType.getMaxWaitTime());
        add(spnMaxWait, bagConstraints);

        bagConstraints.gridx = 5;
        chkEntrance = new JCheckBox("Entrance");
        chkEntrance.setSelected(locationType.isEntrance());
        add(chkEntrance, bagConstraints);

        bagConstraints.gridx = 6;
        chkExit = new JCheckBox("Exit");
        chkExit.setSelected(locationType.isExit());
        add(chkExit, bagConstraints);

        bagConstraints.gridx = 7;
        chkOccupied = new JCheckBox("Occupied");
        chkOccupied.setSelected(locationType.isStartOccupied());
        add(chkOccupied, bagConstraints);

        bagConstraints.gridx = 8;
        btnColor = new ColorButton(locationType.getColor());
        add(btnColor, bagConstraints);

        //jSpinner = new JSpinner();

        bagConstraints.gridx = 9;

        //testLocation.add(jSpinner, bagConstraints);

        JButton closeButton = new JButton("X");
        // TODO: Remove location type when X clicked
        closeButton.setMargin(new Insets(5, 5, 5, 5));

        add(closeButton, bagConstraints);

        bagConstraints.gridx = 0;
        bagConstraints.gridy = 1;
        bagConstraints.gridwidth = 10;
        bagConstraints.weightx = 1;
        bagConstraints.fill = GridBagConstraints.BOTH;

        JPanel pnlTransitions = new JPanel();
        pnlTransitions.setLayout(new BoxLayout(pnlTransitions, BoxLayout.Y_AXIS));

        for (LocationType.Transition transition : locationType.getTransitions()) {
            transitionPanelList.add(new TransitionPanel(transition));
        }

        for (TransitionPanel panel : transitionPanelList) {
            pnlTransitions.add(panel);
        }

        add(pnlTransitions, bagConstraints);

        bagConstraints.gridy = 2;
        bagConstraints.fill = GridBagConstraints.NONE;
        JButton btnAddTransition = new JButton("Add Transition");
        btnAddTransition.addActionListener(e -> {
            LocationType.Transition transition = new LocationType.Transition(locationType, 1, LocationType.SelectionMethod.RANDOM, LocationType.UnavailableBehavior.REPICK);
            locationType.addTransition(transition);
            TransitionPanel transitionPanel = new TransitionPanel(transition);
            transitionPanelList.add(transitionPanel);
            pnlTransitions.add(transitionPanel);
            revalidate();
        });
        add(btnAddTransition, bagConstraints);

        bagConstraints.gridy = 3;
        add(Box.createVerticalStrut(25), bagConstraints);
    }

    public String getLocationName() {
        return txtName.getText();
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void addTransitions(Map<String, LocationType> locationTypeMap) {
        LocationType locationType = locationTypeMap.get(txtName.getText());

        for (TransitionPanel transitionPanel : transitionPanelList) {
            locationType.addTransition(transitionPanel.getTransition(locationTypeMap));
        }
    }

    public void applyValues(Map<String, LocationType> locationTypeMap) throws Exception {
        locationType.setName(txtName.getText());
        //locationType.setMinWaitTime((float)spnMinWait.getValue());
        //locationType.setMaxWaitTime((float)spnMaxWait.getValue());
        locationType.setMinWaitTime(((Number)spnMinWait.getValue()).floatValue());
        locationType.setMaxWaitTime(((Number)spnMaxWait.getValue()).floatValue());
        locationType.setEntrance(chkEntrance.isSelected());
        locationType.setExit(chkExit.isSelected());
        locationType.setStartOccupied(chkOccupied.isSelected());
        locationType.setColor(btnColor.getColor());

        for (TransitionPanel transitionPanel : transitionPanelList) {
            transitionPanel.applyValues(locationTypeMap);
        }

        locationType.refreshProbabilities();
    }
}
