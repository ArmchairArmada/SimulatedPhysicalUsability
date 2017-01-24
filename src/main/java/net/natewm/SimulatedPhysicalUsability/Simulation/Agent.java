package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MeshRenderNodeHandle;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.Navigation.NavigationGrid;
import org.joml.Vector2f;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/4/2017.
 */
public class Agent {
    private static final float WALKING_SPEED = 8.0f;
    private static final float TURN_RATE = 10.0f;
    private static final float RADIUS = 0.3f;
    private static final float FRICTION = 6.0f;
    private static final float AGENT_FRICTION = 0.01f;
    private static final float PUSH = 2.5f;

    MeshRenderNodeHandle renderNodeHandle;
    Transform transform;

    float angle = 360f * (float)Math.random();
    float x = 0f;
    float y = 0f;
    float vx = 0f;
    float vy = 0f;

    float speedVariation = (float)(1.0 + Math.random()*0.2);
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

    public void update(AgentManager agentManager, GraphicsEngine graphicsEngine, GroundGrid groundGrid, CollisionGrid<Agent> collisionGrid, NavigationGrid navigationGrid, float dt) {
        int wallsHit;
        float ox = x;
        float oy = y;

        collisionGrid.remove(x, y, this);

        float px = 0f;
        float py = 0f;
        float distance;
        List<Agent> agents = collisionGrid.getSurroundingList(x, y);
        for (Agent agent : agents) {
            distance = (float) Math.hypot(x - agent.x, y - agent.y);
            //if (rect.isOverlapping(agent.rect)) {
            if (distance <= RADIUS*2) {
                distance = distance * distance * distance * distance;
                px += dt * PUSH * (x - agent.x) / distance;
                py += dt * PUSH * (y - agent.y) / distance;
            }
        }

        distance = (float)Math.hypot(px, py);
        vx -= vx * distance;
        vy -= vy * distance;

        vx += px;
        vy += py;

        vx -= vx * FRICTION * AGENT_FRICTION * dt;
        vy -= vy * FRICTION * AGENT_FRICTION * dt;

        distance = (float)Math.hypot(vx, vy);
        if (distance > 2f) {
            vx = 2f * vx/distance;
            vy = 2f * vy/distance;
        }

        //float turnAmount = ((float)Math.random() - 0.5f) * TURN_RATE * dt;
        //angle += turnAmount;
        //transform.rotation.rotateAxis(turnAmount, 0, 1, 0);

        Vector2f navVec = navigationGrid.getVector(0, x, y);

        vx += navVec.x * WALKING_SPEED * speedVariation * dt;
        vy += navVec.y * WALKING_SPEED * speedVariation * dt;

        angle = (float)Math.atan2(vx, vy);

        //vx += Math.sin(angle) * WALKING_SPEED * speedVariation * dt;
        //vy += Math.cos(angle) * WALKING_SPEED * speedVariation * dt;

        x += vx * dt;
        y += vy * dt;

        //transform.position.add(new Vector3f(transform.forward).mul(WALKING_SPEED * speedVariation * dt).rotate(transform.rotation));

        wallsHit = collisionGrid.hitWall(ox, oy, x, y);
        if ((wallsHit & CollisionGrid.HORIZONTAL) > 0) {
            y = oy;
            vy = -vy;
            angle = (float)Math.atan2(vx, vy);
        }

        if ((wallsHit & CollisionGrid.VERTICAL) > 0) {
            x = ox;
            vx = -vx;
            angle = (float)Math.atan2(vx, vy);
        }

        transform.position.set(x, 0, y);
        //angle = (float)Math.atan2(vx, vy);
        transform.rotation.setAngleAxis(angle, 0f, 1f, 0f);

        rect.x = x - RADIUS;
        rect.y = y - RADIUS;

        groundGrid.add(transform.position, dt);

        collisionGrid.put(x, y, this);

        // TODO: Use real exit locations
        if (x > 0 && x < 1 && y > 0 && y < 1) {
            agentManager.remove(this);
        }

        //transform.updateMatrix();

        //graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);
    }

    public void updateGraphics(GraphicsEngine graphicsEngine) {
        graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);
    }

    public void dispose(GraphicsEngine graphicsEngine, CollisionGrid<Agent> collisionGrid) {
        //renderer.remove(renderNodeHandle);
        graphicsEngine.removeNodeFromRenderer(renderNodeHandle);
        collisionGrid.remove(x, y, this);
    }
}
