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
public class GraphicsPanel extends GLCanvas {
    private GL3 gl;
    private Animator animator;

    Vector3f cameraCenter = new Vector3f();
    float distance = 20;
    float cameraRotateY = 0f;
    float cameraRotateX = -0.8f;
    Quaternionf cameraAngle = new Quaternionf();
    Matrix4f camera = new Matrix4f();

    int button = 0;
    int previousX = 0;
    int previousY = 0;

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
                //System.out.println("PRESSED " + e.getButton());
                button = e.getButton();
                previousX = e.getX();
                previousY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //System.out.println("RELEASED " + e.getButton());
                button = 0;
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
                Vector3f v = new Vector3f();
                Quaternionf q = new Quaternionf();

                if (button == 1) {
                    q.identity().rotateAxis(cameraRotateY, 0f, 1f, 0f);

                    v.set(1f, 0f, 0f);
                    v.rotate(q);
                    //System.out.println(v);
                    v.mul((e.getX() - previousX) * 0.0007f * distance);
                    cameraCenter.add(v);

                    v.set(0f, 0f, 1f);
                    v.rotate(q);
                    //System.out.println(v);
                    v.mul((e.getY() - previousY) * 0.0007f * distance);
                    cameraCenter.add(v);

                    //System.out.println(cameraCenter);
                }
                if (button == 2) {
                    distance += (e.getY() - previousY) * 0.1f;
                    //System.out.println("New Distance " + distance);

                    if (distance < 2f)
                        distance = 2f;
                }
                if (button == 3) {
                    cameraRotateY -= (e.getX() - previousX) * 0.001f;
                    cameraRotateX += (e.getY() - previousY) * 0.001f;
                    if (cameraRotateX > -0.001f)
                        cameraRotateX = -0.001f;
                    if (cameraRotateX < -Math.PI/2.0+0.025)
                        cameraRotateX = (float)(-Math.PI/2.0+0.025);
                    cameraAngle.identity().rotateAxis(cameraRotateY, 0f, 1f, 0f).rotateAxis(cameraRotateX, 1f, 0f, 0f);

                    //System.out.println("New Z " + cameraRotateY + ", X " + cameraRotateX);
                }

                previousX = e.getX();
                previousY = e.getY();

                v.set(0f, distance, 0f);
                v.rotate(cameraAngle);
                v.add(cameraCenter);

                camera.setLookAt(v.x, v.y, v.z, cameraCenter.x, 0.5f, cameraCenter.z, 0, 1, 0);

                //System.out.println("DRAG " + e.getX() + ", " + e.getY());
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
            Mesh mesh, mesh3;
            Material material, floor_material;

            Renderer renderer = new Renderer();
            ArrayList<IRenderNode> nodes = new ArrayList<>();

            Texture texture;

            long lastTime = 0;
            float x = 0.0f;

            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                cameraAngle.identity().rotateAxis(cameraRotateY, 0f, 1f, 0f).rotateAxis(cameraRotateX, 1f, 0f, 0f);
                Vector3f v = new Vector3f(0f, distance, 0f);
                v.rotate(cameraAngle);
                v.add(cameraCenter);

                camera.setLookAt(v.x, v.y, v.z, cameraCenter.x, 0.5f, cameraCenter.z, 0, 1, 0);

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
