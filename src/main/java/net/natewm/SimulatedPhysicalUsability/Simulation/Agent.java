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
    private static final double WALKING_SPEED = 400.0;
    private static final double TURN_RATE = 10.0;
    private static final double RADIUS = 0.35; //0.25;
    //private static final double RADIUS = 0.3f;
    private static final double FRICTION = 450.0;
    private static final double DRUNKENNESS = 700.0;
    //private static final double AGENT_FRICTION = 1.0f;
    //private static final double PUSH = 1.5f;
    private static final double PUSH = 100.0;

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
            //force.add(navVec.x * WALKING_SPEED, navVec.y * WALKING_SPEED);
            //facing = Math.atan2(navVec.x, navVec.y);
            facing = Math.atan2(navVec.x + velocity.x, navVec.y + velocity.y);
            force.add(Math.sin(facing) * WALKING_SPEED, Math.cos(facing) * WALKING_SPEED);
            transform.rotation.nlerp(new Quaternionf().setAngleAxis(facing, 0f, 1f, 0f), 5.0f*dt);
        }

        force.sub(new Vector2d(velocity).mul(velocity.length()*FRICTION));

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
    }

    public void updateGraphics(GraphicsEngine graphicsEngine) {
        graphicsEngine.setRenderNodeTransform(renderNodeHandle, transform);
    }

    public void dispose(GraphicsEngine graphicsEngine, Environment environment) {
        graphicsEngine.removeNodeFromRenderer(renderNodeHandle);
        environment.getAgentCollisionCollection().remove(rect);
    }
}
