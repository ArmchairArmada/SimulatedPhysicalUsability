package net.natewm.SimulatedPhysicalUsability.Simulation;

import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.Information.FloatGrid;
import net.natewm.SimulatedPhysicalUsability.Rendering.MeshRenderNode;
import net.natewm.SimulatedPhysicalUsability.Rendering.Texture;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.Resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

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

        TextureHandle dataTextureHandle;
        FloatGrid floatGrid = new FloatGrid(128, 128);

        public SimulationRunnable(GraphicsEngine graphicsEngine) {
            this.graphicsEngine = graphicsEngine;
        }

        @Override
        public void run() {
            init();

            long time;
            float dt;
            long lastTime = System.nanoTime();

            float runtime = 0f;

            float tmp = 0.05f;

            while (running) {
                time = System.nanoTime();
                dt = (float)((time - lastTime)/1000000000.0);
                lastTime = time;
                runtime += dt;

                frameEnded = false;

                agentManager.update(graphicsEngine, dt);

                tmp -= dt;
                if (tmp < 0f) {
                    tmp += 0.05f;

                    for (int y=0; y<floatGrid.getHeight(); y++) {
                        for (int x=0; x<floatGrid.getWidth(); x++) {
                            //floatGrids.set(x, y, (float)Math.random());
                            //floatGrids.set(x, y, (float)(Math.sin((x+tmp)*0.1 * Math.cos((y-tmp)*0.2)) + Math.sin((y+tmp)*0.1 * Math.cos((x-tmp)*0.2))));
                            floatGrid.set(x, y, (float)(Math.cos(Math.cos(0.2*x+runtime) + Math.cos(0.2*y+runtime) + 0.5*Math.cos(0.005*x*y+runtime))));
                        }
                    }

                    graphicsEngine.updateTexture(dataTextureHandle, floatGrid);
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
            resourceManager = new ResourceManager(graphicsEngine);

            graphicsEngine.setRendererClearColor(new float[]{1f,1f,1f,1f});

            for (int y=0; y<floatGrid.getHeight(); y++) {
                for (int x=0; x<floatGrid.getWidth(); x++) {
                    //floatGrids.set(x, y, (float)Math.random());
                    floatGrid.set(x, y, (float)Math.sin((x-64) * (y-64) * 0.01));
                }
            }

            dataTextureHandle = new TextureHandle();
            graphicsEngine.createTexture(dataTextureHandle, floatGrid, false);
            /*
            try {
                resourceManager.loadTexture(textureHandle, "data/graphics/rgb.png");
            } catch (Exception e) {
                e.printStackTrace();
            }
            */

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
                    graphicsEngine.createMeshRenderNode(node, floorMesh);
                    transform = new Transform();
                    transform.position.set(128*i, 0, 128*j);
                    graphicsEngine.setRenderNodeTransform(node, transform);
                    graphicsEngine.addNodeToRenderer(node);
                }
            }
            graphicsEngine.setMeshNodeTexture(node, dataTextureHandle, 2);

            for (int i=-100; i<101; i++) {
                for (int j=-100; j<101; j++) {
                    node = new MeshRenderNodeHandle();
                    graphicsEngine.createMeshRenderNode(node, agentMesh);

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
    }


    Thread thread;
    SimulationRunnable runnable;


    public SimulationThread(GraphicsEngine graphicsEngine) {
        runnable = new SimulationRunnable(graphicsEngine);
        thread = new Thread(runnable);
    }

    public IFrameEndReciever getFrameEndReciever() {
        return runnable;
    }

    public void start() {
        thread.start();
    }

    public void dispose() {
        runnable.stop();
    }
}
