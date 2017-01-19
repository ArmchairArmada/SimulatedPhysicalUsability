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

    public SimulationControlPanel(SimulationThread simulationThread, GroundGrid groundGrid) {
        JButton button;

        this.simulationThread = simulationThread;
        this.groundGrid = groundGrid;

        button = new JButton("Stop");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationThread.stopSimulation();
                groundGrid.reset();
            }
        });
        add(button);

        button = new JButton("Start");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationThread.startSimulation();
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
                simulationThread.playSimulation(1);
            }
        });
        add(button);

        button = new JButton("Fast");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationThread.playSimulation(2);
            }
        });
        add(button);

        button = new JButton("Faster");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationThread.playSimulation(4);
            }
        });
        add(button);

        button = new JButton("Fastest");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationThread.playSimulation(8);
            }
        });
        add(button);
    }
}
