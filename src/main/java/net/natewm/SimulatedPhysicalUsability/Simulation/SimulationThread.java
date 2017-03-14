package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.ICollisionCollection;
import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Environment.Walls;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Geometry;
import net.natewm.SimulatedPhysicalUsability.Navigation.NavigationGrid;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/9/2017.
 */
public class SimulationThread {
    private class SimulationRunnable implements Runnable, IFrameEndReceiver {
        private Environment environment;
        //private final ICollisionCollection<Agent> collisionCollection;
        //NavigationGrid navigationGrid;
        GraphicsEngine graphicsEngine;
        AgentManager agentManager = new AgentManager();
        //GroundGrid groundGrid;
        //CollisionGrid collisionGrid;
        boolean frameEnded = true;
        boolean running = true;
        boolean paused = false;
        int speed = 1;
        boolean doStop = false;
        boolean doInit = false;
        final Object lock = new Object();

        public SimulationRunnable(GraphicsEngine graphicsEngine, Environment environment) {
            this.graphicsEngine = graphicsEngine;
            this.environment = environment;
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
                    for (int i=0; i<speed; i++) {
                        agentManager.update(graphicsEngine, environment, dt);
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
            MeshHandle agentMesh = new MeshHandle();
            MaterialHandle agentMaterial = new MaterialHandle();

            try {
                graphicsEngine.loadMesh(agentMesh, agentMaterial, "data/graphics/agent.json");
            } catch (Exception e) {
                e.printStackTrace();
            }

            MeshRenderNodeHandle node = new MeshRenderNodeHandle();
            Transform transform;

            for (int i=-14; i<15; i++) {
                for (int j=-29; j<30; j++) {
                    node = new MeshRenderNodeHandle();
                    graphicsEngine.createMeshRenderNode(node, agentMesh, agentMaterial);

                    transform = new Transform();
                    transform.position.set(i*2f+0.5f, 0, j+0.5f);
                    //transform.rotation.setAngleAxis(Math.random()*Math.PI*2.0, 0, 1, 0);
                    graphicsEngine.setRenderNodeTransform(node, transform);
                    graphicsEngine.addNodeToRenderer(node);

                    agentManager.add(new Agent(environment, node, transform));
                }
            }
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
    }


    Thread thread;
    SimulationRunnable runnable;


    public SimulationThread(GraphicsEngine graphicsEngine, Environment environment) {
        runnable = new SimulationRunnable(graphicsEngine, environment);
        thread = new Thread(runnable);
    }

    public IFrameEndReceiver getFrameEndReciever() {
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

    public void startSimulation() {
        runnable.startSimulation();
    }

    public void dispose() {
        runnable.stop();
    }
}
