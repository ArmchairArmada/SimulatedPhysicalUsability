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
    private static final float WALKING_SPEED = 1.39f;
    private static final float TURN_RATE = 25.0f;

    MeshRenderNodeHandle renderNodeHandle;
    Transform transform;
    float speedVariation = (float)(0.5 + Math.random());

    public Agent(MeshRenderNodeHandle renderNodeHandle, Transform transform) {
        this.renderNodeHandle = renderNodeHandle;
        //transform = renderNodeHandle.getTransform();
        this.transform = transform;
    }

    public void update(GraphicsEngine graphicsEngine, GroundGrid groundGrid, float dt) {
        float turnAmount = ((float)Math.random() - 0.5f) * TURN_RATE * dt;
        transform.rotation.rotateAxis(turnAmount, 0, 1, 0);
        transform.position.add(new Vector3f(transform.forward).mul(WALKING_SPEED * speedVariation * dt).rotate(transform.rotation));
        groundGrid.add(transform.position, dt);
        //transform.updateMatrix();

        //graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);
    }

    public void updateGraphics(GraphicsEngine graphicsEngine) {
        graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);
    }

    public void dispose(GraphicsEngine graphicsEngine) {
        //renderer.remove(renderNodeHandle);
        graphicsEngine.removeNodeFromRenderer(renderNodeHandle);
    }
}
