package net.natewm.SimulatedPhysicalUsability.Simulation;

import javafx.util.Pair;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.ICollisionCollection;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;
import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MeshRenderNodeHandle;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.Navigation.NavigationGrid;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/4/2017.
 */
public class Agent {
    private static final float WALKING_SPEED = 8.0f;
    private static final float TURN_RATE = 10.0f;
    private static final float RADIUS = 0.25f;
    //private static final float RADIUS = 0.3f;
    private static final float FRICTION = 3.5f;
    private static final float DRUNKENNESS = 0.5f;
    //private static final float AGENT_FRICTION = 1.0f;
    //private static final float PUSH = 1.5f;
    private static final float PUSH = 0.4f;

    MeshRenderNodeHandle renderNodeHandle;
    Transform transform;

    //int navGridID;
    Location location = null;

    //float angle = 360f * (float)Math.random();
    float x = 0f;
    float y = 0f;
    float vx = 0f;
    float vy = 0f;

    float speedVariation = 1f;//(float)(1.0 + Math.random()*0.2);
    Rect rect = new Rect(0,0, RADIUS*2, RADIUS*2);

    public Agent(Environment environment, MeshRenderNodeHandle renderNodeHandle, Transform transform) {
        this.renderNodeHandle = renderNodeHandle;
        //transform = renderNodeHandle.getTransform();
        this.transform = transform;
        //rect.x = transform.position.x - RADIUS;
        //rect.y = transform.position.z - RADIUS;

        x = transform.position.x;
        y = transform.position.z;

        //navGridID = (int)(Math.random() * navigationGrid.getLocationCount());
        if (environment.getNavigationGrid().getLocationCount() > 0)
            location = environment.getNavigationGrid().getLocation((int)(Math.random() * environment.getNavigationGrid().getLocationCount()));
    }

    public void update(AgentManager agentManager, GraphicsEngine graphicsEngine, Environment environment, float dt) {
        // TODO: Seriously clean this up!!!
        Vector2f navVec = new Vector2f();

        int wallsHit;
        float ox = x;
        float oy = y;

        environment.getAgentCollisionCollection().remove(rect);

        float px = 0f;
        float py = 0f;
        float distance;
        //List<Agent> agents = collisionGrid.getSurroundingList(x, y);
        List<Pair<Rect, Agent>> collisions = new LinkedList<>();
        environment.getAgentCollisionCollection().findOverlapping(rect, collisions);

        Agent agent;
        for (Pair<Rect, Agent> collision : collisions) {
            agent = collision.getValue();
            distance = (float) Math.hypot(x - agent.x, y - agent.y);
            //if (rect.isOverlapping(agent.rect)) {
            if (distance <= RADIUS*2) {
                distance = distance * distance * distance;// * distance;
                distance = distance * distance;
                px += dt * PUSH * (x - agent.x) / distance;
                py += dt * PUSH * (y - agent.y) / distance;

                //vx -= vx * distance * AGENT_FRICTION * dt;
                //vy -= vy * distance * AGENT_FRICTION * dt;
            }
        }

        distance = (float)Math.hypot(px, py);
        vx -= vx * distance;
        vy -= vy * distance;

        vx += px;
        vy += py;

        // TODO: Figure out what I intended to do with AGENT_FRICTION
        //vx -= vx * FRICTION * AGENT_FRICTION * dt;
        //vy -= vy * FRICTION * AGENT_FRICTION * dt;

        vx -= vx * FRICTION * dt;
        vy -= vy * FRICTION * dt;

        distance = (float)Math.hypot(vx, vy);
        if (distance > 3f) {
            vx = 3f * vx/distance;
            vy = 3f * vy/distance;
        }

        //float turnAmount = ((float)Math.random() - 0.5f) * TURN_RATE * dt;
        //angle += turnAmount;
        //transform.rotation.rotateAxis(turnAmount, 0, 1, 0);

        if (location != null) {
            navVec = environment.getNavigationGrid().getVector(location.getNavGridId(), x, y);
            vx += navVec.x * WALKING_SPEED * speedVariation * dt;
            vy += navVec.y * WALKING_SPEED * speedVariation * dt;
        }

        //angle = (float)Math.atan2(vx, vy);

        //vx += Math.sin(angle) * WALKING_SPEED * speedVariation * dt;
        //vy += Math.cos(angle) * WALKING_SPEED * speedVariation * dt;

        float angle = (float) Math.atan2(vx, vy);
        float magnitude = (float) Math.hypot(vx, vy);
        angle += (Math.random() - 0.5)*DRUNKENNESS;
        vx = (float) (magnitude * Math.sin(angle));
        vy = (float) (magnitude * Math.cos(angle));

        x += vx * dt;
        y += vy * dt;

        //transform.position.add(new Vector3f(transform.forward).mul(WALKING_SPEED * speedVariation * dt).rotate(transform.rotation));

        wallsHit = environment.getCollisionGrid().hitWall(ox, oy, x, y);
        if ((wallsHit & CollisionGrid.HORIZONTAL) > 0) {
            y = oy;
            vy = -vy;
            //angle = (float)Math.atan2(vx, vy);
        }

        if ((wallsHit & CollisionGrid.VERTICAL) > 0) {
            x = ox;
            vx = -vx;
            //angle = (float)Math.atan2(vx, vy);
        }

        transform.position.set(x, 0, y);
        //angle = (float)Math.atan2(vx, vy);
        //transform.rotation.setAngleAxis(angle, 0f, 1f, 0f);
        //transform.rotation.setAngleAxis(Math.atan2(navVec.x, navVec.y), 0f, 1f, 0f);
        transform.rotation.nlerp(new Quaternionf().setAngleAxis(Math.atan2(navVec.x, navVec.y), 0f, 1f, 0f), 5.0f*dt);

        rect.x = x - RADIUS;
        rect.y = y - RADIUS;

        environment.getGroundGrid().add(transform.position, dt);

        //collisionGrid.put(x, y, this);
        environment.getAgentCollisionCollection().insert(rect, this);

        // TODO: Use real exit locations
        if (location != null) {
            //System.out.println(location.getX() + ", " + location.getY() + ", " + x + ", " + y);
            if (x > location.getX() && x <= location.getX() + 1 && y > location.getY() && y <= location.getY() + 1) {
                //agentManager.remove(this);
                location = environment.getNavigationGrid().getLocation((int)(Math.random() * environment.getNavigationGrid().getLocationCount()));
            }
        }

        //transform.updateMatrix();

        //graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);
    }

    public void updateGraphics(GraphicsEngine graphicsEngine) {
        graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);
    }

    public void dispose(GraphicsEngine graphicsEngine, Environment environment) {
        //renderer.remove(renderNodeHandle);
        graphicsEngine.removeNodeFromRenderer(renderNodeHandle);
        //collisionGrid.remove(x, y, this);
        environment.getAgentCollisionCollection().remove(rect);
    }
}
