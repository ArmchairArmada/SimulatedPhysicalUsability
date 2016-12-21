package net.natewm.SimulatedPhysicalUsability.Simulation;

/**
 * Created by Nathan on 12/21/2016.
 */
public interface AgentInterface {
    /**
     * Cleanup any resources used by Agent
     */
    public void dispose();

    /**
     * Updates the Agent.
     *
     * @param dt Time delta
     * @return true if still alive, false if should remove
     */
    public boolean update(double dt);
}
