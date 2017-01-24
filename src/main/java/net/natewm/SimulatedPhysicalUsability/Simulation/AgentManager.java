package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Navigation.NavigationGrid;

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

    public void reset(GraphicsEngine graphicsEngine, CollisionGrid<Agent> collisionGrid) {
        for (Agent agent : agents) {
            agent.dispose(graphicsEngine, collisionGrid);
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

    public void update(GraphicsEngine graphicsEngine, GroundGrid groundGrid, CollisionGrid<Agent> collisionGrid, NavigationGrid navigationGrid, float dt) {
        int steps = 1;

        if (dt < 0.1f)
            steps = Math.max((int)(dt/0.017f),1);
        float sdt = dt/steps;

        for (int i=0; i<steps; i++) {
            for (Agent agent : toRemove) {
                agent.dispose(graphicsEngine, collisionGrid);
            }

            agents.removeAll(toRemove);
            toRemove.clear();
            agents.addAll(toAdd);
            toAdd.clear();

            for (Agent agent : agents) {
                agent.update(this, graphicsEngine, groundGrid, collisionGrid, navigationGrid, sdt);

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
