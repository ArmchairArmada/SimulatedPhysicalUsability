package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import com.jogamp.opengl.GL3;

/**
 * Interface for actions the graphics engine can perform.
 */
public interface IGraphicsAction {
    public boolean doIt(GL3 gl);
}
