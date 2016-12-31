package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import org.joml.Matrix4f;

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
public class GraphicsPanel extends GLCanvas {
    private GL3 gl;
    private Animator animator;

    public GraphicsPanel(GLCapabilities glCapabilities) {
        super(glCapabilities);

        setPreferredSize(new Dimension(1000, 600));

        // TODO: Create camera controls
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("PRESSED " + e.getButton());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("RELEASED " + e.getButton());
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("DRAG " + e.getX() + ", " + e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                System.out.println("WHEEL " + e.getScrollAmount());
            }
        });


        addGLEventListener(new GLEventListener() {
            Mesh mesh;
            Material material;

            Renderer renderer = new Renderer();
            ArrayList<RenderNode> nodes = new ArrayList<>();

            Matrix4f camera;

            Texture texture;

            long lastTime = 0;
            float x = 0.0f;

            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                camera = new Matrix4f();
                camera.setLookAt(10, 40, 15, 0, -5, 0, 0, 1, 0);
                //camera.setLookAt(2, 5, 3, 0, 0, 0, 0, 1, 0);

                try {
                    material = Material.loadFromFiles(gl, "data/graphics/vertex_shader.glsl", "data/graphics/fragment_shader.glsl");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                IGeometryLoader geometryLoader = new OBJLoader();
                Geometry geometry = geometryLoader.load("data/graphics/agent.obj");

                gl.glEnable(gl.GL_DEPTH_TEST);
                gl.glEnable(gl.GL_CULL_FACE);

                try {
                    BufferedImage bufferedImage = ImageIO.read(new File("data/graphics/rgb.png"));
                    texture = new Texture(gl, bufferedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                material.addTexture(gl, texture);
                material.use(gl);

                gl.glUniform3f(material.getUniformLocation(gl, "ambient"), 0.3f, 0.3f, 0.3f);

                gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

                int renderGroup = renderer.createRenderGroup();
                int renderGroup2 = renderer.createRenderGroup();

                try {
                    mesh = new Mesh(gl, geometry, material);

                    geometry = geometryLoader.load("data/graphics/monkey_lopoly.obj");
                    Mesh mesh2 = new Mesh(gl, geometry, material);

                    for (int i=-150; i<151; i++) {
                        for (int j=-150; j<151; j++) {
                            RenderNode node;
                            if ((i+j) % 2 == 0) {
                                node = new RenderNode(mesh);
                                renderer.add(renderGroup, node);
                            }
                            else {
                                node = new RenderNode(mesh2);
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

                camera.setLookAt(10f-x, 40f - x, 15, 0, -5, 0, 0, 1, 0);

                //for (RenderNode node : nodes) {
                nodes.parallelStream().forEach((node) -> {
                    node.getTransform().rotation.identity().rotateAxis(x, 1f, 0f, 0f).rotateAxis(x, 0f, 1f, 0f).rotateAxis(x, 0f, 0f, 1f);
                    node.getTransform().updateMatrix();
                });

                renderer.render(gl, camera);
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
