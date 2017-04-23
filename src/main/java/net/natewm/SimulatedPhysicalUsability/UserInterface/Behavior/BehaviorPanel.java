package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;
import net.natewm.SimulatedPhysicalUsability.UserInterface.Environment.EnvironmentControlPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathan on 3/7/2017.
 */
public class BehaviorPanel extends JPanel {
    Environment environment;
    private final ProjectData projectData;
    EnvironmentControlPanel environmentControlPanel;
    ArrayList<LocationTypePanel> locationTypePanels = new ArrayList<>();
    JPanel tilesPanel;

    public BehaviorPanel(Environment environment, ProjectData projectData, EnvironmentControlPanel environmentControlPanel) {
        this.environment = environment;
        this.projectData = projectData;
        this.environmentControlPanel = environmentControlPanel;

        JPanel testLocation;
        JTextField testName;
        ColorButton colorButton;
        JSpinner jSpinner;
        JButton closeButton;

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        //contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setPreferredSize(new Dimension(800,600));
        scrollPane.setBorder(null);

        //setBackground(Color.WHITE);

        tilesPanel = new JPanel();
        tilesPanel.setLayout(new BoxLayout(tilesPanel, BoxLayout.Y_AXIS));

        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.ipadx = 2;
        bagConstraints.ipady = 2;

        /*
        for (int i=0; i<50; i++) {
            LocationTypePanel locationTypePanel = new LocationTypePanel();
            locationTypePanels.add(locationTypePanel);
            tilesPanel.add(locationTypePanel);
        }
        */
        populateLocationTypes();

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
        //spacer.setBackground(Color.WHITE);
        spacer.setPreferredSize(new Dimension(0,0));

        contentPanel.add(spacer, gridBagConstraints);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void populateLocationTypes() {
        tilesPanel.removeAll();
        locationTypePanels.clear();
        for (LocationType locationType : projectData.getLocationTypes()) {
            LocationTypePanel locationTypePanel = new LocationTypePanel(locationType);
            locationTypePanels.add(locationTypePanel);
            tilesPanel.add(locationTypePanel);
        }
        revalidate();
    }

    public void addLocationType() {
        LocationType locationType = new LocationType();
        locationType.setName("Unnamed");
        // TODO: Add location type to environment (or some list of location types)
        LocationTypePanel locationTypePanel = new LocationTypePanel(locationType);
        locationTypePanels.add(locationTypePanel);
        tilesPanel.add(locationTypePanel);
        projectData.addLocationType(locationType);
        revalidate();
    }

    public void applyValues() throws Exception {
        Map<String, LocationType> locationTypeMap = new HashMap<>();

        for (LocationTypePanel locationTypePanel : locationTypePanels) {
            if (locationTypeMap.get(locationTypePanel.getLocationName()) == null)
                locationTypeMap.put(locationTypePanel.getLocationName(), locationTypePanel.getLocationType());
            else
                throw new Exception("Duplicate Location Type Names");
        }

        for (LocationTypePanel locationTypePanel : locationTypePanels) {
            locationTypePanel.applyValues(locationTypeMap);
        }
    }
}
