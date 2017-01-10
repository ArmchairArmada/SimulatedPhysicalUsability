package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.IFrameEndReciever;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.MeshHandle;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.MeshRenderNodeHandle;
import net.natewm.SimulatedPhysicalUsability.Rendering.MeshRenderNode;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
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
        boolean frameEnded = true;
        boolean running = true;

        public SimulationRunnable(GraphicsEngine graphicsEngine) {
            this.graphicsEngine = graphicsEngine;
        }

        @Override
        public void run() {
            init();

            long time;
            float dt;
            long lastTime = System.nanoTime();

            while (running) {
                time = System.nanoTime();
                dt = (float)((time - lastTime)/1000000000.0);
                lastTime = time;

                frameEnded = false;

                agentManager.update(graphicsEngine, dt);
                graphicsEngine.frameEnd();

                while (!frameEnded) {
                    try {
                        sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void init() {
            resourceManager = new ResourceManager(graphicsEngine);

            graphicsEngine.setRendererClearColor(new float[]{1f,1f,1f,1f});

            //MeshHandle monkeyMesh = resourceManager.loadMesh(graphicsEngine, "data/graphics/monkey.json");
            //MeshHandle agentMesh = resourceManager.loadMesh(graphicsEngine, "data/graphics/agent.json");
            //MeshHandle floorMesh = resourceManager.loadMesh(graphicsEngine, "data/graphics/floor.json");

            MeshHandle monkeyMesh = new MeshHandle();
            MeshHandle agentMesh = new MeshHandle();
            MeshHandle floorMesh = new MeshHandle();

            try {
                resourceManager.loadMesh(monkeyMesh, "data/graphics/monkey.json");
                resourceManager.loadMesh(agentMesh, "data/graphics/agent.json");
                resourceManager.loadMesh(floorMesh, "data/graphics/floor.json");
            } catch (Exception e) {
                e.printStackTrace();
            }


            MeshRenderNodeHandle node = new MeshRenderNodeHandle();
            Transform transform;
            for (int i=-8; i<9; i++) {
                for (int j=-8; j<9; j++) {
                    //node = new MeshRenderNode(floorMesh);
                    graphicsEngine.createMeshRenderNode(node, floorMesh);
                    transform = new Transform();
                    transform.position.set(128*i, 0, 128*j);
                    graphicsEngine.setRenderNodeTransform(node, transform);
                    graphicsEngine.addNodeToRenderer(node);
                    //renderer.add(node);
                }
            }

            //renderer.add(floorGroup, new MeshRenderNode(floorMesh));
            //floorHandle = new RenderNodeHandle(new MeshRenderNode(floorMesh));
            //renderingSystem.addRenderNode(floorGroup, floorHandle);

            for (int i=-150; i<151; i++) {
                for (int j=-150; j<151; j++) {
                        /*
                        if ((i+j) % 2 == 0) {
                            node = new MeshRenderNode(monkeyMesh);
                            monkeyHandle = new RenderNodeHandle(new MeshRenderNode(monkeyMesh));
                            //renderingSystem.addRenderNode(monkeyGroup, monkeyHandle);
                            //renderingSystem.setRenderNodePosition(monkeyHandle, new Vector3f(i, 1, j));
                            renderer.add(monkeyGroup, node);
                            node.getTransform().position.set(i, 1, j);
                        }
                        else {
                        */
                    //node = new MeshRenderNode(agentMesh);
                    node = new MeshRenderNodeHandle();
                    graphicsEngine.createMeshRenderNode(node, agentMesh);
                    //agentHandle = new RenderNodeHandle(new MeshRenderNode(agentMesh));
                    //renderingSystem.addRenderNode(agentGroup, agentHandle);
                    //renderingSystem.setRenderNodePosition(agentHandle, new Vector3f(i, 1, j));
                    //renderer.add(node);
                    //node.getTransform().position.set(i, 0, j);
                    //node.getTransform().rotation.setAngleAxis(Math.random()*Math.PI*2.0, 0, 1, 0);

                    transform = new Transform();
                    transform.position.set(i, 0, j);
                    transform.rotation.setAngleAxis(Math.random()*Math.PI*2.0, 0, 1, 0);
                    graphicsEngine.setRenderNodeTransform(node, transform);
                    graphicsEngine.addNodeToRenderer(node);
                    //}

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
    }

    SimulationRunnable runnable;


    public SimulationThread(GraphicsEngine graphicsEngine) {
        runnable = new SimulationRunnable(graphicsEngine);
    }

    public IFrameEndReciever getFrameEndReciever() {
        return runnable;
    }

    public void start() {
        Thread t = new Thread(runnable);
        t.start();
    }
}
