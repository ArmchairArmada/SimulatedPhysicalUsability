package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.Environment.Walls;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.Resources.Geometry;
import net.natewm.SimulatedPhysicalUsability.Resources.ResourceManager;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/9/2017.
 */
public class SimulationThread {
    private class SimulationRunnable implements Runnable, IFrameEndReciever {
        GraphicsEngine graphicsEngine;
        ResourceManager resourceManager;
        AgentManager agentManager = new AgentManager();
        GroundGrid groundGrid;
        boolean frameEnded = true;
        boolean running = true;
        boolean paused = false;
        int speed = 1;
        boolean doStop = false;
        boolean doInit = false;

        public SimulationRunnable(GraphicsEngine graphicsEngine, ResourceManager resourceManager, GroundGrid groundGrid) {
            this.graphicsEngine = graphicsEngine;
            this.resourceManager = resourceManager;
            this.groundGrid = groundGrid;
        }

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

                frameEnded = false;

                if (doStop) {
                    doStop = false;
                    agentManager.reset(graphicsEngine);
                }

                if (doInit) {
                    doInit = false;
                    init();
                }

                if (!paused) {
                    for (int i=0; i<speed; i++) {
                        agentManager.update(graphicsEngine, groundGrid, dt);
                    }

                    groundGrid.update();
                }

                graphicsEngine.frameEnd();

                while (!frameEnded) {
                    try {
                        sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            dispose();
        }

        private void init() {
            // TODO: Remove this temp garbage
            Walls walls = new Walls();
            Geometry geometry = walls.generateGeometry();
            MeshHandle wallMesh = new MeshHandle();
            MaterialHandle wallMaterial = new MaterialHandle();
            MeshRenderNodeHandle wallNode = new MeshRenderNodeHandle();
            try {
                resourceManager.loadMaterial(wallMaterial, "data/graphics/wall_material.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
            graphicsEngine.createMesh(wallMesh, geometry);
            graphicsEngine.createMeshRenderNode(wallNode, wallMesh, wallMaterial);
            graphicsEngine.addNodeToRenderer(wallNode);


            MeshHandle agentMesh = new MeshHandle();
            MaterialHandle agentMaterial = new MaterialHandle();

            try {
                resourceManager.loadMesh(agentMesh, agentMaterial, "data/graphics/agent.json");
            } catch (Exception e) {
                e.printStackTrace();
            }

            MeshRenderNodeHandle node = new MeshRenderNodeHandle();
            Transform transform;

            for (int i=-50; i<51; i++) {
                for (int j=-50; j<51; j++) {
                    node = new MeshRenderNodeHandle();
                    graphicsEngine.createMeshRenderNode(node, agentMesh, agentMaterial);

                    transform = new Transform();
                    transform.position.set(i, 0, j);
                    transform.rotation.setAngleAxis(Math.random()*Math.PI*2.0, 0, 1, 0);
                    graphicsEngine.setRenderNodeTransform(node, transform);
                    graphicsEngine.addNodeToRenderer(node);

                    agentManager.add(new Agent(node, transform));
                }
            }
        }

        private void dispose() {
            resourceManager.disposeAll();
        }

        @Override
        public void graphicsFrameEnded() {
            frameEnded = true;
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


    public SimulationThread(GraphicsEngine graphicsEngine, ResourceManager resourceManager, GroundGrid groundGrid) {
        runnable = new SimulationRunnable(graphicsEngine, resourceManager, groundGrid);
        thread = new Thread(runnable);
    }

    public IFrameEndReciever getFrameEndReciever() {
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
