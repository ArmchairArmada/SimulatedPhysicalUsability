package net.natewm.SimulatedPhysicalUsability.UserInterface;

import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nathan on 1/18/2017.
 */
public class SimulationControlPanel extends JPanel {
    private SimulationThread simulationThread;
    private GroundGrid groundGrid;

    private boolean stopped = true;
    private int speed = 1;

    public SimulationControlPanel(SimulationThread simulationThread, GroundGrid groundGrid) {
        JButton button;

        this.simulationThread = simulationThread;
        this.groundGrid = groundGrid;

        button = new JButton("Stop");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopped = true;
                speed = 1;
                simulationThread.stopSimulation();
                groundGrid.reset();
            }
        });
        add(button);

        button = new JButton("Pause");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationThread.pauseSimulation();
            }
        });
        add(button);

        button = new JButton("Play");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stopped) {
                    stopped = false;
                    simulationThread.startSimulation();
                    groundGrid.reset();
                }
                simulationThread.playSimulation(speed);
            }
        });
        add(button);

        button = new JButton("Slower");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                speed--;
                if (speed < 1)
                    speed = 1;

                simulationThread.playSimulation(speed);
            }
        });
        add(button);

        button = new JButton("Faster");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                speed++;
                simulationThread.playSimulation(speed);
            }
        });
        add(button);
    }
}
