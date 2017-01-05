package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.Rendering.MeshRenderNode;
import net.natewm.SimulatedPhysicalUsability.Rendering.Renderer;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Created by Nathan on 1/4/2017.
 */
public class Agent {
    MeshRenderNode renderNode;
    Transform transform;

    public Agent(MeshRenderNode renderNode) {
        this.renderNode = renderNode;
        transform = renderNode.getTransform();
    }

    public void update(float dt) {
        float turnAmount = ((float)Math.random() - 0.5f) * 20.0f * dt;
        transform.rotation.rotateAxis(turnAmount, 0, 1, 0);
        transform.position.add(new Vector3f(transform.forward).mul(dt).rotate(transform.rotation));
        transform.updateMatrix();
    }

    public void dispose(Renderer renderer) {
        renderer.remove(renderNode);
    }
}
