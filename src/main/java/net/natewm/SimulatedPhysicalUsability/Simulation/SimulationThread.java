package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;

/**
 * Created by Nathan on 1/9/2017.
 */
public class SimulationThread {
    private class SimulationRunnable implements Runnable, IFrameEndReceiver {
        private final Environment environment;
        private final ProjectData projectData;
        //private final ICollisionCollection<Agent> collisionCollection;
        //NavigationGrid navigationGrid;
        final GraphicsEngine graphicsEngine;
        final AgentManager agentManager = new AgentManager();
        MeshHandle agentMesh = null;
        MaterialHandle agentMaterial = null;
        //GroundGrid groundGrid;
        //CollisionGrid collisionGrid;
        boolean frameEnded = true;
        boolean running = true;
        boolean paused = true;
        int speed = 1;
        float enterRate = 1.0f;
        boolean doStop = false;
        boolean doInit = false;
        final Object lock = new Object();

        public SimulationRunnable(GraphicsEngine graphicsEngine, Environment environment, ProjectData projectData) {
            this.graphicsEngine = graphicsEngine;
            this.environment = environment;
            this.projectData = projectData;
        }

        /*
        public SimulationRunnable(GraphicsEngine graphicsEngine, GroundGrid groundGrid, CollisionGrid collisionGrid, ICollisionCollection<Agent> collisionCollection, NavigationGrid navigationGrid) {
            this.graphicsEngine = graphicsEngine;
            this.groundGrid = groundGrid;
            this.collisionGrid = collisionGrid;
            this.collisionCollection = collisionCollection;
            this.navigationGrid = navigationGrid;
        }
        */

        @Override
        public void run() {
            //init();

            long time;
            float dt;
            long lastTime = System.nanoTime();

            float runtime = 0f;

            while (running) {
                time = System.nanoTime();
                dt = (float)((time - lastTime)/1000000000.0);
                lastTime = time;
                runtime += dt;

                if (dt > 0.03f)
                    dt = 0.03f;

                frameEnded = false;

                if (doStop) {
                    doStop = false;
                    agentManager.reset(graphicsEngine, environment);
                }

                if (doInit) {
                    doInit = false;
                    init();
                }

                if (!paused) {
                    // TODO: Allow for adjusting spawn probability
                    for (int i=0; i<speed; i++) {
                        if (Math.random() < dt*enterRate) {
                            Location location = environment.getRandomEntrance();
                            if (location != null && location.getLocationType() != null)
                                createAgent(location.getX(), location.getY(), location.getLocationType().randomTransition(projectData));
                        }

                        agentManager.update(graphicsEngine, environment, projectData, dt);
                    }

                    //groundGrid.update();
                    environment.getGroundGrid().update();
                }

                graphicsEngine.frameEnd();

                /*
                while (!frameEnded) {
                    try {
                        sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                */
                synchronized (lock) {
                    while (!frameEnded) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            dispose();
        }

        private void init() {
            agentMesh = new MeshHandle();
            agentMaterial = new MaterialHandle();

            try {
                graphicsEngine.loadMesh(agentMesh, agentMaterial, "data/graphics/agent.json");
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Location location : projectData.getLocations()) {
                location.leave();

                if (location.getLocationType().isStartOccupied()) {
                    createAgent(location.getX(), location.getY(), location);
                }
            }

            //MeshRenderNodeHandle node;// = new MeshRenderNodeHandle();
            //Transform transform;

            /*
            for (int i=-14; i<15; i++) {
                for (int j=-14; j<15; j++) {
                    node = new MeshRenderNodeHandle();
                    graphicsEngine.createMeshRenderNode(node, agentMesh, agentMaterial);

                    transform = new Transform();
                    //transform.position.set(i*2f+0.5f, 0, j*2f+0.5f);
                    transform.position.set(i*2f+0.5f, 0, j*2f+0.5f);
                    //transform.rotation.setAngleAxis(Math.random()*Math.PI*2.0, 0, 1, 0);
                    graphicsEngine.setRenderNodeTransform(node, transform);
                    graphicsEngine.addNodeToRenderer(node);

                    agentManager.add(new Agent(environment, node, transform, environment.getRandomLocation("exit")));
                }
            }
            */
        }

        private void createAgent(float x, float y, Location location) {
            MeshRenderNodeHandle node = new MeshRenderNodeHandle();
            graphicsEngine.createMeshRenderNode(node, agentMesh, agentMaterial);

            Transform transform = new Transform();
            transform.position.set(x+(float)Math.random(), 0, y+(float)Math.random());
            graphicsEngine.setRenderNodeTransform(node, transform);
            graphicsEngine.addNodeToRenderer(node);

            agentManager.add(new Agent(node, transform, location));
        }

        private void dispose() {
            graphicsEngine.removeAllNodesFromRenderer();
        }

        @Override
        public void graphicsFrameEnded() {
            synchronized (lock) {
                frameEnded = true;
                lock.notify();
            }
        }

        public void stop() {
            running = false;
        }

        public synchronized void stopSimulation() {
            paused = true;
            doStop = true;
            //agentManager.reset(graphicsEngine);
        }

        public synchronized void startSimulation() {
            //agentManager.reset(graphicsEngine);
            //init();
            doStop = true;
            doInit = true;
            paused = false;
            speed = 1;
        }

        public synchronized void pauseSimulation() {
            paused = true;
        }

        public synchronized void playSimulation(int speed) {
            this.speed = speed;
            paused = false;
        }

        public synchronized void setEnterRate(float rate) {
            enterRate = rate;
        }
    }


    private final Thread thread;
    private final SimulationRunnable runnable;


    public SimulationThread(GraphicsEngine graphicsEngine, Environment environment, ProjectData projectData) {
        runnable = new SimulationRunnable(graphicsEngine, environment, projectData);
        thread = new Thread(runnable);
    }

    public IFrameEndReceiver getFrameEndReceiver() {
        return runnable;
    }

    public void start() {
        thread.start();
    }

    public void stopSimulation() {
        runnable.stopSimulation();
    }

    public void pauseSimulation() {
        runnable.pauseSimulation();
    }

    public void playSimulation(int speed) {
        runnable.playSimulation(speed);
    }

    public void setEnterRate(float rate) {
        runnable.setEnterRate(rate);
    }

    public void startSimulation() {
        runnable.startSimulation();
    }

    public void dispose() {
        runnable.stop();
    }
}
