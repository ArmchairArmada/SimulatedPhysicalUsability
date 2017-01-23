package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MeshRenderNodeHandle;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Transform;
import org.joml.Vector3f;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/4/2017.
 */
public class Agent {
    private static final float WALKING_SPEED = 20.0f;
    private static final float TURN_RATE = 100.0f;
    private static final float RADIUS = 0.5f;
    private static final float FRICTION = 10.0f;
    private static final float PUSH = 2.0f;

    MeshRenderNodeHandle renderNodeHandle;
    Transform transform;

    float angle = 360f * (float)Math.random();
    float x = 0f;
    float y = 0f;
    float vx = 0f;
    float vy = 0f;

    float speedVariation = (float)(0.25 + Math.random());
    Rect rect = new Rect(0,0, RADIUS*2, RADIUS*2);

    public Agent(MeshRenderNodeHandle renderNodeHandle, Transform transform) {
        this.renderNodeHandle = renderNodeHandle;
        //transform = renderNodeHandle.getTransform();
        this.transform = transform;
        rect.x = transform.position.x - RADIUS;
        rect.y = transform.position.z - RADIUS;

        x = transform.position.x;
        y = transform.position.z;
    }

    public void update(GraphicsEngine graphicsEngine, GroundGrid groundGrid, CollisionGrid<Agent> collisionGrid, float dt) {
        float ox = x;
        float oy = y;

        collisionGrid.remove(x, y, this);

        float distance;
        List<Agent> agents = collisionGrid.getList(rect);
        for (Agent agent : agents) {
            if (rect.isOverlapping(agent.rect)) {
                distance = (float) Math.hypot(x - agent.x, y - agent.y);
                distance = distance * distance * distance;
                vx += dt * PUSH * (x - agent.x) / distance;
                vy += dt * PUSH * (y - agent.y) / distance;
            }
        }

        float turnAmount = ((float)Math.random() - 0.5f) * TURN_RATE * dt;
        angle += turnAmount;
        //transform.rotation.rotateAxis(turnAmount, 0, 1, 0);

        vx += Math.sin(angle) * WALKING_SPEED * speedVariation * dt;
        vy += Math.cos(angle) * WALKING_SPEED * speedVariation * dt;

        vx -= vx * FRICTION * dt;
        vy -= vy * FRICTION * dt;

        x += vx * dt;
        y += vy * dt;

        //transform.position.add(new Vector3f(transform.forward).mul(WALKING_SPEED * speedVariation * dt).rotate(transform.rotation));

        if (collisionGrid.hitWall(ox, oy, x, y)) {
            x = ox;
            y = oy;
            vx = -vx * 0.1f;
            vy = -vy * 0.1f;
            //angle = angle + 180f;
        }

        transform.position.set(x, 0, y);
        angle = (float)Math.atan2(vx, vy);
        transform.rotation.setAngleAxis(angle, 0f, 1f, 0f);

        rect.x = x - RADIUS;
        rect.y = y - RADIUS;

        groundGrid.add(transform.position, dt);

        collisionGrid.put(x, y, this);

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
