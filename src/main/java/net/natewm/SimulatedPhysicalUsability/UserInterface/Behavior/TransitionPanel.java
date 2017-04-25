package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Map;

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

    private LocationType.Transition transition;

    public TransitionPanel(LocationType.Transition transition) {
        super();

        this.transition = transition;

        setLayout(new GridBagLayout());

        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.ipadx = 2;
        bagConstraints.ipady = 2;

        bagConstraints.fill = GridBagConstraints.VERTICAL;
        bagConstraints.gridx = 0;
        bagConstraints.weightx = 0;
        add(Box.createHorizontalStrut(20));

        txtDestination = new JTextField(transition.getDestination().getName());

        bagConstraints.fill = GridBagConstraints.BOTH;
        bagConstraints.gridx = 1;
        bagConstraints.weightx = 1;

        add(txtDestination, bagConstraints);

        bagConstraints.weightx = 0;
        bagConstraints.fill = GridBagConstraints.VERTICAL;
        bagConstraints.gridx = 2;

        add(new JLabel("Weight"), bagConstraints);

        bagConstraints.gridx = 3;
        spnWeight = new JSpinner(new SpinnerNumberModel(1, 0, 10000,1));
        ((JSpinner.DefaultEditor)spnWeight.getEditor()).getTextField().setColumns(5);
        spnWeight.setValue(transition.getWeight());
        add(spnWeight, bagConstraints);

        /*
        //jSpinner = new JSpinner();
        cmbSelection = new JComboBox<String>(selectionStrings);
        cmbSelection.setSelectedItem(LocationType.getSelectionMethodString(transition.getSelectionMethod()));

        bagConstraints.gridx = 4;

        add(cmbSelection, bagConstraints);

        cmbUnavailable = new JComboBox<String>(unavailableStrings);
        cmbUnavailable.setSelectedItem(LocationType.getUnavailableBehaviorString(transition.getUnavailableBehavior()));

        bagConstraints.gridx = 5;

        add(cmbUnavailable, bagConstraints);
        */

        JButton closeButton = new JButton("X");
        closeButton.setMargin(new Insets(5, 5, 5, 5));

        //bagConstraints.gridx = 6;
        bagConstraints.gridx = 4;

        add(closeButton, bagConstraints);
    }

    public LocationType.Transition getTransition(Map<String, LocationType> locationTypeMap) {
        LocationType.SelectionMethod selectionMethod =  LocationType.SelectionMethod.RANDOM;
        LocationType.UnavailableBehavior unavailableBehavior = LocationType.UnavailableBehavior.REPICK;

        selectionMethod = LocationType.getSelectionMethodFromString(getSelection());
        unavailableBehavior = LocationType.getUnavailableBehaviorFromString(getUnavailable());

        return new LocationType.Transition(locationTypeMap.get(getDestination()), getWeight(), selectionMethod, unavailableBehavior);
    }

    public String getDestination() {
        return txtDestination.getText();
    }

    public int getWeight() {
        try {
            spnWeight.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int)spnWeight.getValue();
    }

    public String getSelection() {
        return (String)cmbSelection.getSelectedItem();
    }

    public String getUnavailable() {
        return (String)cmbUnavailable.getSelectedItem();
    }

    public void applyValues(Map<String, LocationType> locationTypeMap) throws Exception {
        LocationType destination = locationTypeMap.get(txtDestination.getText());
        if (destination == null) {
            throw new Exception("Invalid destination name.");
        }
        else {
            transition.setDestination(destination);
            transition.setWeight((int)spnWeight.getValue());
            //transition.setSelectionMethod(LocationType.getSelectionMethodFromString((String)cmbSelection.getSelectedItem()));
            //transition.setUnavailableBehavior(LocationType.getUnavailableBehaviorFromString((String)cmbUnavailable.getSelectedItem()));
        }
    }
}
