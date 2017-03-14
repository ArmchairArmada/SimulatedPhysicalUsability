package net.natewm.SimulatedPhysicalUsability.UserInterface.Simulation;

import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nathan on 1/18/2017.
 */
public class SimulationControlPanel extends JPanel {
    private SimulationThread simulationThread;
    private Environment environment;

    private boolean stopped = true;
    private int speed = 1;

    public SimulationControlPanel(SimulationThread simulationThread, Environment environment) {
        JButton button;

        this.simulationThread = simulationThread;
        this.environment = environment;

        button = new JButton("Stop");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopped = true;
                speed = 1;
                simulationThread.stopSimulation();
                environment.getGroundGrid().reset();
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
                    environment.getGroundGrid().reset();
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
