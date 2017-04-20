package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Environment.EnvironmentControlPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nathan on 3/7/2017.
 */
public class BehaviorPanel extends JPanel {
    Environment environment;
    EnvironmentControlPanel environmentControlPanel;
    ArrayList<LocationTypePanel> locationTypePanels = new ArrayList<>();

    public BehaviorPanel(Environment environment, EnvironmentControlPanel environmentControlPanel) {
        this.environment = environment;
        this.environmentControlPanel = environmentControlPanel;

        JPanel testLocation;
        JTextField testName;
        ColorButton colorButton;
        JSpinner jSpinner;
        JButton closeButton;

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setPreferredSize(new Dimension(800,600));
        scrollPane.setBorder(null);

        setBackground(Color.WHITE);

        JPanel tilesPanel = new JPanel();
        tilesPanel.setLayout(new BoxLayout(tilesPanel, BoxLayout.Y_AXIS));

        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.ipadx = 2;
        bagConstraints.ipady = 2;
        for (int i=0; i<50; i++) {
            LocationTypePanel locationTypePanel = new LocationTypePanel();
            locationTypePanels.add(locationTypePanel);
            tilesPanel.add(locationTypePanel);
        }

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;

        contentPanel.add(tilesPanel, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_END;

        JPanel spacer = new JPanel();
        spacer.setBackground(Color.WHITE);
        spacer.setPreferredSize(new Dimension(0,0));

        contentPanel.add(spacer, gridBagConstraints);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void applyChanges() {
        ArrayList<LocationType> locationTypes = new ArrayList<>();
        HashMap<String, LocationType> locationTypeHashMap = new HashMap<>();
        LocationType locationType;

        for (LocationTypePanel locationTypePanel : locationTypePanels) {
            locationType = locationTypePanel.getLocationType();
            locationTypes.add(locationType);
            locationTypeHashMap.put(locationTypePanel.getLocationName(), locationType);
        }

        for (LocationTypePanel locationTypePanel : locationTypePanels) {
            locationTypePanel.addTransitions(locationTypeHashMap);
        }

        environmentControlPanel.addButtons(locationTypes);
    }
}
