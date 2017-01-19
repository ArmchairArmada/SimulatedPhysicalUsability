package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/4/2017.
 */
public class AgentManager {
    List<Agent> agents = new ArrayList<>();
    List<Agent> toAdd = new ArrayList<>();
    List<Agent> toRemove = new ArrayList<>();

    public AgentManager() {
    }

    public void reset(GraphicsEngine graphicsEngine) {
        for (Agent agent : agents) {
            agent.dispose(graphicsEngine);
        }

        agents = new ArrayList<>();
        toAdd = new ArrayList<>();
        toRemove = new ArrayList<>();
    }

    public void add(Agent agent) {
        toAdd.add(agent);
    }

    public void remove(Agent agent) {
        toRemove.add(agent);
    }

    public void update(GraphicsEngine graphicsEngine, GroundGrid groundGrid, float dt) {
        int steps = 1;

        if (dt < 0.1f)
            steps = Math.max((int)(dt/0.017f),1);
        float sdt = dt/steps;

        for (int i=0; i<steps; i++) {
            for (Agent agent : toRemove) {
                agent.dispose(graphicsEngine);
            }

            agents.removeAll(toRemove);
            toRemove.clear();
            agents.addAll(toAdd);
            toAdd.clear();

            for (Agent agent : agents) {
                agent.update(graphicsEngine, groundGrid, sdt);

                //if (Math.random() < 0.01)
                //    remove(agent);
            }
        }

        for (Agent agent : agents) {
            agent.updateGraphics(graphicsEngine);
        }

        //System.out.println(agents.size());
    }
}
