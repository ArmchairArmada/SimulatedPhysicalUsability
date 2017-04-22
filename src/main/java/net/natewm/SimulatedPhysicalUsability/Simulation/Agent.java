package net.natewm.SimulatedPhysicalUsability.Simulation;

import javafx.util.Pair;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;
import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MeshRenderNodeHandle;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nathan on 1/4/2017.
 */
public class Agent {
    private static final double WALKING_SPEED = 450.0;
    private static final double TURN_RATE = 10.0;
    private static final double RADIUS = 0.35; //0.25;
    //private static final double RADIUS = 0.3f;
    private static final double FRICTION = 250.0;
    private static final double DRUNKENNESS = 300.0;
    //private static final double AGENT_FRICTION = 1.0f;
    //private static final double PUSH = 1.5f;
    private static final double PUSH = 20.0;

    MeshRenderNodeHandle renderNodeHandle;
    Transform transform;

    //int navGridID;
    Location location = null;

    //float angle = 360f * (float)Math.random();
    double facing = 0.0;
    Vector2d position = new Vector2d();
    Vector2d velocity = new Vector2d();
    Vector2d acceleration = new Vector2d();

    //double x = 0f;
    //double y = 0f;
    //double vx = 0f;
    //double vy = 0f;

    //double speedVariation = 1f;//(float)(1.0 + Math.random()*0.2);
    Rect rect = new Rect(0,0, (float)(RADIUS*2), (float)(RADIUS*2));

    public Agent(Environment environment, MeshRenderNodeHandle renderNodeHandle, Transform transform, Location location) {
        this.renderNodeHandle = renderNodeHandle;
        this.transform = transform;

        //x = transform.position.x;
        //y = transform.position.z;

        position.x = transform.position.x;
        position.y = transform.position.z;

        this.location = location;
    }

    public void update(AgentManager agentManager, GraphicsEngine graphicsEngine, Environment environment, ProjectData projectData, float dt) {
        Vector2d force = new Vector2d();
        Vector2d difference = new Vector2d();
        Vector2d oldPosition = new Vector2d(position);
        Agent agent;
        double distance;

        environment.getAgentCollisionCollection().remove(rect);

        List<Pair<Rect, Agent>> collisions = new LinkedList<>();
        environment.getAgentCollisionCollection().findOverlapping(rect, collisions);

        for (Pair<Rect, Agent> collision : collisions) {
            agent = collision.getValue();
            difference.set(position).sub(agent.position);
            distance = difference.length();
            if (distance <= RADIUS*2) {
                force.add(difference.mul(PUSH/Math.pow(distance, 5.0)));
            }
        }

        if (location != null) {
            Vector2f navVec = environment.getNavigationGrid().getVector(location.getNavGridId(), (float)position.x, (float)position.y);
            force.add(navVec.x * WALKING_SPEED, navVec.y * WALKING_SPEED);
            //facing = Math.atan2(navVec.x, navVec.y);
            facing = Math.atan2(navVec.x + velocity.x, navVec.y + velocity.y);
            transform.rotation.nlerp(new Quaternionf().setAngleAxis(facing, 0f, 1f, 0f), 5.0f*dt);
        }

        force.sub(new Vector2d(velocity).mul(FRICTION));

        if (force.length() > 5000.0) {
            force.normalize().mul(5000.0);
        }

        //force.add((Math.random()-0.5)*DRUNKENNESS, (Math.random()-0.5)*DRUNKENNESS);
        double angle = Math.random() * Math.PI * 2.0;
        double mag = Math.random() * DRUNKENNESS;
        force.add(mag*Math.cos(angle), mag*Math.sin(angle));

        acceleration.set(force).mul(dt);
        velocity.add(new Vector2d(acceleration).mul(dt));
        position.add(new Vector2d(velocity).mul(dt));

        int wallsHit = environment.getCollisionGrid().hitWall((float)oldPosition.x, (float)oldPosition.y, (float)position.x, (float)position.y);
        if ((wallsHit & CollisionGrid.HORIZONTAL) > 0) {
            position.y = oldPosition.y;
            velocity.y = -velocity.y;
        }

        if ((wallsHit & CollisionGrid.VERTICAL) > 0) {
            position.x = oldPosition.x;
            velocity.x = -velocity.x;
        }

        transform.position.set((float)position.x, 0, (float)position.y);

        rect.x = (float)(position.x - RADIUS);
        rect.y = (float)(position.y - RADIUS);

        environment.getGroundGrid().add(transform.position, dt);

        environment.getAgentCollisionCollection().insert(rect, this);

        // TODO: Use real exit locations
        if (location != null) {
            if (location.isInRange((float)position.x, (float)position.y)) {
                if (location.isExit()) {
                    agentManager.remove(this);
                }
                else {
                    location = location.getLocationType().randomTransition(projectData);
                }
            }
        }

        /*
        // TODO: Seriously clean this up!!!
        Vector2f navVec = new Vector2f();

        int wallsHit;
        double ox = x;
        double oy = y;

        environment.getAgentCollisionCollection().remove(rect);

        double px = 0f;
        double py = 0f;
        double distance;

        List<Pair<Rect, Agent>> collisions = new LinkedList<>();
        environment.getAgentCollisionCollection().findOverlapping(rect, collisions);

        Agent agent;
        for (Pair<Rect, Agent> collision : collisions) {
            agent = collision.getValue();
            distance = Math.hypot(x - agent.x, y - agent.y);

            if (distance <= RADIUS*2) {
                distance = distance * distance * distance;// * distance;
                distance = distance * distance;
                px += dt * PUSH * (x - agent.x) / distance;
                py += dt * PUSH * (y - agent.y) / distance;
            }
        }

        distance = Math.hypot(px, py);
        vx -= vx * distance;
        vy -= vy * distance;

        vx += px;
        vy += py;

        vx -= vx * FRICTION * dt;
        vy -= vy * FRICTION * dt;

        distance = (float)Math.hypot(vx, vy);
        if (distance > 3f) {
            vx = 3.0 * vx/distance;
            vy = 3.0 * vy/distance;
        }

        if (location != null) {
            navVec = environment.getNavigationGrid().getVector(location.getNavGridId(), (float)x, (float)y);
            vx += navVec.x * WALKING_SPEED * speedVariation * dt;
            vy += navVec.y * WALKING_SPEED * speedVariation * dt;
        }

        float angle = (float) Math.atan2(vx, vy);
        float magnitude = (float) Math.hypot(vx, vy);
        angle += (Math.random() - 0.5)*DRUNKENNESS;
        vx = (float) (magnitude * Math.sin(angle));
        vy = (float) (magnitude * Math.cos(angle));

        x += vx * dt;
        y += vy * dt;

        wallsHit = environment.getCollisionGrid().hitWall((float)ox, (float)oy, (float)x, (float)y);
        if ((wallsHit & CollisionGrid.HORIZONTAL) > 0) {
            y = oy;
            vy = -vy;
        }

        if ((wallsHit & CollisionGrid.VERTICAL) > 0) {
            x = ox;
            vx = -vx;
        }

        transform.position.set((float)x, 0, (float)y);
        transform.rotation.nlerp(new Quaternionf().setAngleAxis(Math.atan2(navVec.x, navVec.y), 0f, 1f, 0f), 5.0f*dt);

        rect.x = (float)(x - RADIUS);
        rect.y = (float)(y - RADIUS);

        environment.getGroundGrid().add(transform.position, dt);

        environment.getAgentCollisionCollection().insert(rect, this);

        // TODO: Use real exit locations
        if (location != null) {
            if (location.isInRange((float)x, (float)y)) {
                if (location.isExit()) {
                    agentManager.remove(this);
                }
                else {
                    location = location.getLocationType().randomTransition(environment);
                }
            }
        }
        */
    }

    public void updateGraphics(GraphicsEngine graphicsEngine) {
        graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);
    }

    public void dispose(GraphicsEngine graphicsEngine, Environment environment) {
        graphicsEngine.removeNodeFromRenderer(renderNodeHandle);
        environment.getAgentCollisionCollection().remove(rect);
    }
}
