package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
            Mesh mesh, mesh3;
            Material material, floor_material;

            Renderer renderer = new Renderer();
            ArrayList<IRenderNode> nodes = new ArrayList<>();

            Texture texture;

            long lastTime = 0;
            float x = 0.0f;

            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                //camera.setLookAt(10, 40, 15, 0, -5, 0, 0, 1, 0);
                //camera.setLookAt(2, 5, 3, 0, 0, 0, 0, 1, 0);

                IGeometryLoader geometryLoader = new OBJLoader();
                Geometry geometry = geometryLoader.load("data/graphics/agent.obj");

                gl.glEnable(gl.GL_DEPTH_TEST);
                gl.glEnable(gl.GL_CULL_FACE);

                try {
                    material = Material.loadFromFiles(gl, "data/graphics/vertex_shader.glsl", "data/graphics/fragment_shader.glsl");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    BufferedImage bufferedImage = ImageIO.read(new File("data/graphics/rgb.png"));
                    texture = new Texture(gl, bufferedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                material.addTexture(gl, texture);

                try {
                    mesh = new Mesh(gl, geometry, material);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    floor_material = Material.loadFromFiles(gl, "data/graphics/vertex_shader.glsl", "data/graphics/fragment_shader2.glsl");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    BufferedImage bufferedImage = ImageIO.read(new File("data/graphics/floor_tile.png"));
                    texture = new Texture(gl, bufferedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                floor_material.addTexture(gl, texture);



                gl.glUniform3f(material.getUniformLocation(gl, "ambient"), 0.3f, 0.3f, 0.3f);

                gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);


                int renderGroup = renderer.createRenderGroup();

                try {
                    geometry = geometryLoader.load("data/graphics/floor.obj");
                    mesh3 = new Mesh(gl, geometry, floor_material);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                renderer.add(renderGroup, new MeshRenderNode(mesh3));

                renderGroup = renderer.createRenderGroup();


                int renderGroup2 = renderer.createRenderGroup();

                try {
                    geometry = geometryLoader.load("data/graphics/monkey_lopoly.obj");
                    Mesh mesh2 = new Mesh(gl, geometry, material);

                    for (int i=-20; i<21; i++) {
                        for (int j=-20; j<21; j++) {
                            MeshRenderNode node;
                            if ((i+j) % 2 == 0) {
                                node = new MeshRenderNode(mesh);
                                renderer.add(renderGroup, node);
                            }
                            else {
                                node = new MeshRenderNode(mesh2);
                                renderer.add(renderGroup2, node);
                            }
                            node.getTransform().position.set(i, 0, j);
                            nodes.add(node);

                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                animator.setUpdateFPSFrames(60, System.out);
                lastTime = System.nanoTime();
            }

            public void dispose(GLAutoDrawable glAutoDrawable) {
                mesh.dispose(gl);
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
                renderer.setProjection(45.0f, width, height, 0.1f, 1000.0f);
            }
        });

        animator = new Animator(this);
        animator.start();
    }
}
