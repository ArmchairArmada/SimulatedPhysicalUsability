package net.natewm.SimulatedPhysicalUsability.Rendering;

/**
 * Created by Nathan on 1/4/2017.
 */

/*
    This is not currently used.  It was part of an experiment with RenderingSystem to make OpenGL rendering threaded.

    Other files that can be removed:
        - RenderingSystem
 */

public class RenderNodeHandle {
    IRenderNode renderNode;

    public RenderNodeHandle(IRenderNode renderNode) {
        this.renderNode = renderNode;
    }
}
