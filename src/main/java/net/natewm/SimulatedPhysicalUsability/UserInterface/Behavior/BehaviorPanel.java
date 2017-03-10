package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * Created by Nathan on 3/7/2017.
 */
public class BehaviorPanel extends JPanel {
    public BehaviorPanel() {
        JPanel testLocation;
        JTextField testName;
        JButton colorButton;
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
            testLocation = new JPanel();
            testLocation.setLayout(new GridBagLayout());
            //testLocation.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            testName = new JTextField("Name of the Location");

            bagConstraints.fill = GridBagConstraints.BOTH;
            bagConstraints.gridx = 0;
            bagConstraints.weightx = 1;

            testLocation.add(testName, bagConstraints);

            colorButton = new JButton();
            colorButton.setMargin(new Insets(0, 0, 0, 0));
            colorButton.setIcon(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x, y, 17, 17);

                    //g.setColor(Color.BLACK);
                    //g.drawRect(x,y,32,24);
                }

                @Override
                public int getIconWidth() {
                    return 16;
                }

                @Override
                public int getIconHeight() {
                    return 16;
                }
            });

            bagConstraints.weightx = 0;
            bagConstraints.fill = GridBagConstraints.VERTICAL;
            bagConstraints.gridx = 1;

            testLocation.add(colorButton, bagConstraints);

            jSpinner = new JSpinner();

            bagConstraints.gridx = 2;

            testLocation.add(jSpinner, bagConstraints);

            closeButton = new JButton("X");
            closeButton.setMargin(new Insets(5, 5, 5, 5));

            bagConstraints.gridx = 3;

            testLocation.add(closeButton, bagConstraints);

            tilesPanel.add(testLocation);
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
