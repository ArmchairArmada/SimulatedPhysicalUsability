package net.natewm.SimulatedPhysicalUsability.UserInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import net.natewm.SimulatedPhysicalUsability.Rendering.*;
import net.natewm.SimulatedPhysicalUsability.Resources.*;
import net.natewm.SimulatedPhysicalUsability.Simulation.Agent;
import net.natewm.SimulatedPhysicalUsability.Simulation.AgentManager;
import org.joml.Vector3f;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Nathan on 12/22/2016.
 */

// TODO: Clean up this mess
public class GraphicsPanel extends GLCanvas {
    private GL3 gl;
    private Animator animator;

    private MouseCamera mouseCamera;

    public GraphicsPanel(GLCapabilities glCapabilities) {
        super(glCapabilities);

        setPreferredSize(new Dimension(1000, 600));

        mouseCamera = new MouseCamera(-0.8f, 0f, 20f);

        // TODO: Create camera controls
        addMouseListener(mouseCamera.getMouseListener());
        addMouseMotionListener(mouseCamera.getMouseMotionListener());
        addMouseWheelListener(mouseCamera.getMouseWheelListener());


        addGLEventListener(new GLEventListener() {
            ResourceManager resourceManager;
            Mesh monkeyMesh, agentMesh, floorMesh;
            int monkeyGroup, agentGroup, floorGroup;

            //RenderNodeHandle monkeyHandle, agentHandle, floorHandle;

            Renderer renderer = new Renderer();
            //RenderingSystem renderingSystem;
            //ArrayList<IRenderNode> nodes = new ArrayList<>();
            //ArrayList<Agent> agents = new ArrayList<Agent>();
            AgentManager agentManager = new AgentManager(renderer);

            Texture texture;

            long lastTime = 0;
            float x = 0.0f;

            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                //renderingSystem = new RenderingSystem(gl);

                renderer.init(gl);

                resourceManager = new ResourceManager(gl, new ObjectMapper());

                monkeyGroup = renderer.createRenderGroup();
                //monkeyGroup = renderingSystem.createRenderGroup();
                try {
                    monkeyMesh = resourceManager.loadMesh("data/graphics/monkey.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                agentGroup = renderer.createRenderGroup();
                //agentGroup = renderingSystem.createRenderGroup();
                try {
                    agentMesh = resourceManager.loadMesh("data/graphics/agent.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                floorGroup = renderer.createRenderGroup();
                //floorGroup = renderingSystem.createRenderGroup();
                try {
                    floorMesh = resourceManager.loadMesh("data/graphics/floor.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MeshRenderNode node;
                for (int i=-8; i<9; i++) {
                    for (int j=-8; j<9; j++) {
                        node = new MeshRenderNode(floorMesh);
                        node.getTransform().position.set(128*i, 0, 128*j);
                        node.getTransform().updateMatrix();
                        renderer.add(floorGroup, node);
                    }
                }

                //renderer.add(floorGroup, new MeshRenderNode(floorMesh));
                //floorHandle = new RenderNodeHandle(new MeshRenderNode(floorMesh));
                //renderingSystem.addRenderNode(floorGroup, floorHandle);

                for (int i=-100; i<101; i++) {
                    for (int j=-100; j<101; j++) {
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
                            node = new MeshRenderNode(agentMesh);
                            //agentHandle = new RenderNodeHandle(new MeshRenderNode(agentMesh));
                            //renderingSystem.addRenderNode(agentGroup, agentHandle);
                            //renderingSystem.setRenderNodePosition(agentHandle, new Vector3f(i, 1, j));
                            renderer.add(agentGroup, node);
                            node.getTransform().position.set(i, 0, j);
                            node.getTransform().rotation.setAngleAxis(Math.random()*Math.PI*2.0, 0, 1, 0);
                        //}

                        agentManager.add(new Agent(node));
                    }
                }

                animator.setUpdateFPSFrames(60, System.out);
                lastTime = System.nanoTime();
            }

            public void dispose(GLAutoDrawable glAutoDrawable) {
                //renderingSystem.stop();
                resourceManager.disposeAll();
            }

            public void display(GLAutoDrawable glAutoDrawable) {
                long time = System.nanoTime();
                float dt = (float)((time - lastTime)/1000000000.0);
                lastTime = time;

                agentManager.update(dt);

                renderer.render(gl, mouseCamera.getMatrix());
            }

            public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
                renderer.reshape(gl, x, y, width, height);
            }
        });

        animator = new Animator(this);
        animator.start();
    }
}
