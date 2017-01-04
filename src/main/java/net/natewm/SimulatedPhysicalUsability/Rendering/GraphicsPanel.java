package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import net.natewm.SimulatedPhysicalUsability.Resources.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nathan on 12/22/2016.
 */

// TODO: Clean up this mess
public class GraphicsPanel extends GLCanvas {
    private GL3 gl;
    private Animator animator;

    MouseCamera mouseCamera;

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
            Material material, floor_material;

            Renderer renderer = new Renderer();
            ArrayList<IRenderNode> nodes = new ArrayList<>();

            Texture texture;

            long lastTime = 0;
            float x = 0.0f;

            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                gl.glEnable(gl.GL_DEPTH_TEST);
                gl.glEnable(gl.GL_CULL_FACE);
                gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

                resourceManager = new ResourceManager(gl, new ObjectMapper());

                monkeyGroup = renderer.createRenderGroup();
                try {
                    monkeyMesh = resourceManager.loadMesh("data/graphics/monkey.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                agentGroup = renderer.createRenderGroup();
                try {
                    agentMesh = resourceManager.loadMesh("data/graphics/agent.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                floorGroup = renderer.createRenderGroup();
                try {
                    floorMesh = resourceManager.loadMesh("data/graphics/floor.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                renderer.add(floorGroup, new MeshRenderNode(floorMesh));


                for (int i=-20; i<21; i++) {
                    for (int j=-20; j<21; j++) {
                        MeshRenderNode node;
                        if ((i+j) % 2 == 0) {
                            node = new MeshRenderNode(monkeyMesh);
                            renderer.add(monkeyGroup, node);
                        }
                        else {
                            node = new MeshRenderNode(agentMesh);
                            renderer.add(agentGroup, node);
                        }
                        node.getTransform().position.set(i, 0, j);
                        nodes.add(node);

                    }
                }

                animator.setUpdateFPSFrames(60, System.out);
                lastTime = System.nanoTime();
            }

            public void dispose(GLAutoDrawable glAutoDrawable) {
                resourceManager.disposeAll();
            }

            public void display(GLAutoDrawable glAutoDrawable) {
                long time = System.nanoTime();
                float dt = (time - lastTime)/1000000.0f;
                lastTime = time;

                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
                x += 0.001f * dt;

                //camera.setLookAt(10f-x, 40f - x, 15, 0, -5, 0, 0, 1, 0);

                //for (MeshRenderNode node : nodes) {
                nodes.parallelStream().forEach((node) -> {
                    node.getTransform().rotation.identity().rotateAxis(x, 0f, 1f, 0f);
                    node.getTransform().updateMatrix();
                });

                renderer.render(gl, mouseCamera.getMatrix());
            }

            public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
                gl.glViewport(0, 0, width, height);
                renderer.setProjection(49.0f, width, height, 0.1f, 1000.0f);
            }
        });

        animator = new Animator(this);
        animator.start();
    }
}
