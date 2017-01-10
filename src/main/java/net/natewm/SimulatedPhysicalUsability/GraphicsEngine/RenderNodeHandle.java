package net.natewm.SimulatedPhysicalUsability.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.Rendering.IRenderNode;

/**
 * Created by Nathan on 1/9/2017.
 */
public class RenderNodeHandle {
    IRenderNode renderNode = null;

    public void set(RenderNodeHandle renderNodeHandle) {
        renderNode = renderNodeHandle.renderNode;
    }

    public IRenderNode getRenderNode() {
        return renderNode;
    }
}
