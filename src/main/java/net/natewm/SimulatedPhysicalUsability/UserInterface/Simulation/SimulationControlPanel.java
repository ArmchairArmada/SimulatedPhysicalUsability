package net.natewm.SimulatedPhysicalUsability.UserInterface.Simulation;

import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by Nathan on 1/18/2017.
 */
public class SimulationControlPanel extends JPanel {
    private final JLabel lblSpeed;

    private boolean stopped = true;
    private int speed = 1;

    public SimulationControlPanel(SimulationThread simulationThread, Environment environment) {
        JButton button;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        lblSpeed = new JLabel("Speed: 1");
        lblSpeed.setAlignmentX(CENTER_ALIGNMENT);

        button = new JButton("Stop");
        button.addActionListener(e -> {
            stopped = true;
            speed = 1;
            lblSpeed.setText("Speed: " + speed);
            simulationThread.stopSimulation();
            environment.getGroundGrid().reset();
        });
        button.setAlignmentX(CENTER_ALIGNMENT);
        add(button);

        button = new JButton("Pause");
        button.addActionListener(e -> simulationThread.pauseSimulation());
        button.setAlignmentX(CENTER_ALIGNMENT);
        add(button);

        button = new JButton("Play");
        button.addActionListener(e -> {
            if (stopped) {
                stopped = false;
                simulationThread.startSimulation();
                environment.getGroundGrid().reset();
            }
            simulationThread.playSimulation(speed);
        });
        button.setAlignmentX(CENTER_ALIGNMENT);
        add(button);

        button = new JButton("Slower");
        button.addActionListener(e -> {
            //speed--;
            speed /= 2;
            if (speed < 1)
                speed = 1;

            lblSpeed.setText("Speed: " + speed);
            simulationThread.playSimulation(speed);
        });
        button.setAlignmentX(CENTER_ALIGNMENT);
        add(button);

        button = new JButton("Faster");
        button.addActionListener(e -> {
            //speed++;
            speed *= 2;
            lblSpeed.setText("Speed: " + speed);
            simulationThread.playSimulation(speed);
        });
        button.setAlignmentX(CENTER_ALIGNMENT);
        add(button);

        add(lblSpeed);

        JPanel pnlEnterRate = new JPanel();
        pnlEnterRate.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblEnterRate = new JLabel("Enter Rate:");
        pnlEnterRate.add(lblEnterRate);

        JSpinner spnEnterRate = new JSpinner();
        spnEnterRate.setModel(new SpinnerNumberModel(1.0, 0.0, 100.0, 0.01));
        ((JSpinner.DefaultEditor)spnEnterRate.getEditor()).getTextField().setColumns(5);
        spnEnterRate.addChangeListener(e -> simulationThread.setEnterRate(((Double)spnEnterRate.getValue()).floatValue()));
        pnlEnterRate.add(spnEnterRate);

        add(pnlEnterRate);
    }

    public void reset() {
        stopped = true;
        speed = 1;
    }
}
