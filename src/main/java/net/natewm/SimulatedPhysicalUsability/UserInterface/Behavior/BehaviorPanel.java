package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 3/7/2017.
 */
public class BehaviorPanel extends JPanel {
    public BehaviorPanel() {
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
        scrollPane.setPreferredSize(new Dimension(800,600));
        scrollPane.setBorder(null);

        setBackground(Color.WHITE);

        JPanel tilesPanel = new JPanel();
        tilesPanel.setLayout(new BoxLayout(tilesPanel, BoxLayout.Y_AXIS));

        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.ipadx = 2;
        bagConstraints.ipady = 2;
        for (int i=0; i<50; i++) {
            tilesPanel.add(new LocationTypePanel());
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
}
