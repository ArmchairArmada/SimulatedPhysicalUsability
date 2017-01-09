package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Graphics.GraphicsEngine;

/**
 * Created by Nathan on 1/5/2017.
 */
public interface IAction {
    public boolean doIt(GL3 gl, GraphicsEngine engine);
}
