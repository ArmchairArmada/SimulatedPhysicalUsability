package net.natewm.SimulatedPhysicalUsability.UserInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import net.natewm.SimulatedPhysicalUsability.Rendering.*;
import net.natewm.SimulatedPhysicalUsability.Resources.*;

import java.awt.*;
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
            Material material, floor_material;

            Renderer renderer = new Renderer();
            ArrayList<IRenderNode> nodes = new ArrayList<>();

            Texture texture;

            long lastTime = 0;
            float x = 0.0f;

            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                renderer.init(gl);

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

                for (int i=-30; i<31; i++) {
                    for (int j=-30; j<31; j++) {
                        MeshRenderNode node;
                        if ((i+j) % 2 == 0) {
                            node = new MeshRenderNode(monkeyMesh);
                            renderer.add(monkeyGroup, node);
                            node.getTransform().position.set(i, 1, j);
                        }
                        else {
                            node = new MeshRenderNode(agentMesh);
                            renderer.add(agentGroup, node);
                            node.getTransform().position.set(i, 0, j);
                        }

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

                x += 0.001f * dt;

                nodes.parallelStream().forEach((node) -> {
                    node.getTransform().rotation.identity().rotateAxis(x, 0f, 1f, 0f);
                    //node.getTransform().position.add(new Vector3f(0f, 0f, dt*0.001f).rotate(node.getTransform().rotation));
                    node.getTransform().updateMatrix();
                });

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
