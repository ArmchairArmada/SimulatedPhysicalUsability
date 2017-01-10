package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.IFrameEndReciever;
import net.natewm.SimulatedPhysicalUsability.Rendering.Renderer;

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

    public void add(Agent agent) {
        toAdd.add(agent);
    }

    public void remove(Agent agent) {
        toRemove.add(agent);
    }

    public void update(GraphicsEngine graphicsEngine, float dt) {
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

            //agents.parallelStream().forEach((agent) -> {
            for (Agent agent : agents) {
                agent.update(graphicsEngine, sdt);

                if (Math.random() < sdt*0.1) {
                    remove(agent);
                }
            //});
            }
        }

        //System.out.println(agents.size());
    }
}
