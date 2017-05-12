package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine;

import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.IRenderNode;

/**
 * Handle for render nodes.
 */
public class RenderNodeHandle {
    IRenderNode renderNode = null;

    /**
     * Sets the render node in the handle to be the same as that of the given handle.
     *
     * @param renderNodeHandle Handle to use for setting the render node.
     */
    public void set(RenderNodeHandle renderNodeHandle) {
        renderNode = renderNodeHandle.renderNode;
    }

    /**
     * Gets the render node from the handle.
     *
     * @return Render node in the handle.
     */
    public IRenderNode getRenderNode() {
        return renderNode;
    }

    /**
     * Sets the render node in the handle.
     *
     * @param renderNode Render node the handle should use.
     */
    public void setRenderNode(IRenderNode renderNode) {
        this.renderNode = renderNode;
    }
}
