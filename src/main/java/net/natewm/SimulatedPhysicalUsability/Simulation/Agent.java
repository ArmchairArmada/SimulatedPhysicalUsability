package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.MeshRenderNodeHandle;
import net.natewm.SimulatedPhysicalUsability.Rendering.Renderer;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
import org.joml.Vector3f;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/4/2017.
 */
public class Agent {
    MeshRenderNodeHandle renderNodeHandle;
    Transform transform;

    public Agent(MeshRenderNodeHandle renderNodeHandle, Transform transform) {
        this.renderNodeHandle = renderNodeHandle;
        //transform = renderNodeHandle.getTransform();
        this.transform = transform;
    }

    public void update(GraphicsEngine graphicsEngine, float dt) {
        float turnAmount = ((float)Math.random() - 0.5f) * 20.0f * dt;
        transform.rotation.rotateAxis(turnAmount, 0, 1, 0);
        transform.position.add(new Vector3f(transform.forward).mul(dt).rotate(transform.rotation));
        //transform.updateMatrix();

        graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);

    }

    public void dispose(GraphicsEngine graphicsEngine) {
        //renderer.remove(renderNodeHandle);
        graphicsEngine.removeNodeFromRenderer(renderNodeHandle);
    }
}
