package net.natewm.SimulatedPhysicalUsability.Simulation;

import com.jogamp.opengl.GL;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.Information.FloatGrid;
import net.natewm.SimulatedPhysicalUsability.Rendering.Material;
import net.natewm.SimulatedPhysicalUsability.Rendering.MeshRenderNode;
import net.natewm.SimulatedPhysicalUsability.Rendering.Texture;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
import net.natewm.SimulatedPhysicalUsability.Resources.ResourceManager;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/9/2017.
 */
public class SimulationThread {
    private static final int GROUND_WIDTH = 8;
    private static final int GROUND_HEIGHT = 8;
    private static final int GROUND_GRID_WIDTH = 128;
    private static final int GROUND_GRID_HEIGHT = 128;

    private class SimulationRunnable implements Runnable, IFrameEndReciever {
        GraphicsEngine graphicsEngine;
        ResourceManager resourceManager;
        AgentManager agentManager = new AgentManager();
        GroundGrid groundGrid;
        boolean frameEnded = true;
        boolean running = true;

        //TextureHandle dataTextureHandle;
        //FloatGrid floatGrid = new FloatGrid(128, 128);

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

                agentManager.update(graphicsEngine, groundGrid, dt);
                groundGrid.update();

                /*
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
                */

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

            /*
            for (int y=0; y<floatGrid.getHeight(); y++) {
                for (int x=0; x<floatGrid.getWidth(); x++) {
                    //floatGrids.set(x, y, (float)Math.random());
                    floatGrid.set(x, y, (float)Math.sin((x-64) * (y-64) * 0.01));
                }
            }

            dataTextureHandle = new TextureHandle();
            graphicsEngine.createTexture(dataTextureHandle, floatGrid, false);

            TextureHandle tmpHandle = new TextureHandle();
            try {
                resourceManager.loadTexture(tmpHandle, "data/graphics/heatmap2.png");
            } catch (Exception e) {
                e.printStackTrace();
            }
            */

            /*
            try {
                resourceManager.loadTexture(textureHandle, "data/graphics/rgb.png");
            } catch (Exception e) {
                e.printStackTrace();
            }
            */

            //MeshHandle monkeyMesh = new MeshHandle();
            //MaterialHandle monkeyMaterial = new MaterialHandle();

            MeshHandle agentMesh = new MeshHandle();
            MaterialHandle agentMaterial = new MaterialHandle();

            MeshHandle floorMesh = new MeshHandle();
            MaterialHandle floorMaterial = new MaterialHandle();

            try {
                //resourceManager.loadMesh(monkeyMesh, "data/graphics/monkey.json");
                resourceManager.loadMesh(agentMesh, agentMaterial, "data/graphics/agent.json");
                resourceManager.loadMesh(floorMesh, floorMaterial, "data/graphics/floor.json");
            } catch (Exception e) {
                e.printStackTrace();
            }

            graphicsEngine.setMaterialTextureOptions(floorMaterial, 1, GL.GL_CLAMP_TO_EDGE, GL.GL_CLAMP_TO_EDGE, GL.GL_LINEAR, GL.GL_LINEAR);

            groundGrid = new GroundGrid(graphicsEngine, floorMesh, floorMaterial, GROUND_WIDTH, GROUND_HEIGHT, GROUND_GRID_WIDTH, GROUND_GRID_HEIGHT);
            //groundGrid.set(new Vector3f(0,0,0), 1.0f);
            //groundGrid.set(new Vector3f(1,0,1), 1.0f);
            //groundGrid.set(new Vector3f(150,0,0), 1.0f);
            //groundGrid.set(new Vector3f(200,0,200), 1.0f);
            //groundGrid.set(new Vector3f(205,0,205), 1.0f);
            //groundGrid.set(new Vector3f(206,0,206), 2.0f);
            //groundGrid.update();

            MeshRenderNodeHandle node = new MeshRenderNodeHandle();
            Transform transform;
            /*
            for (int i=-8; i<9; i++) {
                for (int j=-8; j<9; j++) {
                    graphicsEngine.createMeshRenderNode(node, floorMesh, floorMaterial);

                    transform = new Transform();
                    transform.position.set(128*i, 0, 128*j);
                    graphicsEngine.setRenderNodeTransform(node, transform);
                    //graphicsEngine.addNodeToRenderer(node);
                    graphicsEngine.addDynamicNodeToRenderer(node);

                    graphicsEngine.makeMeshNodeMaterialUnique(node);
                    graphicsEngine.setMeshNodeTexture(node, dataTextureHandle, 2);
                    if ((i + j) % 2 == 0)
                        graphicsEngine.setMeshNodeTexture(node, tmpHandle, 1);
                }
            }
            */

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
