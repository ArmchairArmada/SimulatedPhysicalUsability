package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Nathan on 12/22/2016.
 */
public class GraphicsPanel extends GLCanvas {
    private GL3 gl;
    private Animator animator;

    private float x = 0.0f;

    public GraphicsPanel(GLCapabilities glCapabilities) {
        super(glCapabilities);

        setPreferredSize(new Dimension(1000, 600));

        addGLEventListener(new GLEventListener() {
            Mesh mesh;
            Material material;

            Scene scene = new Scene();
            SceneNode node1;
            SceneNode node2;

            //Matrix4f projection;
            Matrix4f camera;
            //Matrix4f model;

            Texture texture;

            Matrix4f modelView = new Matrix4f();

            public void init(GLAutoDrawable glAutoDrawable) {
                gl = glAutoDrawable.getGL().getGL3();

                //projection = new Matrix4f();
                camera = new Matrix4f();
                camera.setLookAt(0, 2, 3, 0, 0, 0, 0, 1, 0);

                //model = new Matrix4f();

                try {
                    material = Material.loadFromFiles(gl, "data/graphics/vertex_shader.glsl", "data/graphics/fragment_shader.glsl");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*
                Geometry geometry = new Geometry();
                geometry.addVertex(new Vector3f(-0.5f, 0.5f, 0.0f));
                geometry.addVertex(new Vector3f(-0.5f, -0.5f, 0.0f));
                geometry.addVertex(new Vector3f(0.5f, -0.5f, 0.0f));
                geometry.addVertex(new Vector3f(0.5f, 0.5f, 0.0f));

                geometry.addColor(new Vector3f(1.0f, 0.0f, 0.0f));
                geometry.addColor(new Vector3f(0.0f, 1.0f, 0.0f));
                geometry.addColor(new Vector3f(0.0f, 0.0f, 1.0f));
                geometry.addColor(new Vector3f(1.0f, 1.0f, 1.0f));

                geometry.addTriangle(new Triangle(0, 1, 2));
                geometry.addTriangle(new Triangle(0, 2, 3));
                */

                IGeometryLoader geometryLoader = new OBJLoader();
                Geometry geometry = geometryLoader.load("data/graphics/monkey_uv.obj");

                gl.glEnable(gl.GL_DEPTH_TEST);
                gl.glEnable(gl.GL_CULL_FACE);

                try {
                    BufferedImage bufferedImage = ImageIO.read(new File("data/graphics/alpha.png"));
                    texture = new Texture(gl, bufferedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                material.addTexture(gl, texture);
                material.use(gl);

                //gl.glUniform1i(material.getUniformLocation(gl, "theTexture"), 0);
                //gl.glActiveTexture(gl.GL_TEXTURE0);
                //gl.glBindTexture(gl.GL_TEXTURE_2D, texture.getTextureID());

                gl.glUniform3f(material.getUniformLocation(gl, "ambient"), 0.3f, 0.3f, 0.3f);

                try {
                    mesh = new Mesh(gl, geometry, material);
                    node1 = new SceneNode(mesh);
                    node2 = new SceneNode(mesh);
                    scene.add(node1);
                    scene.add(node2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                animator.setUpdateFPSFrames(60, System.out);
            }

            public void dispose(GLAutoDrawable glAutoDrawable) {
                mesh.dispose(gl);
            }

            public void display(GLAutoDrawable glAutoDrawable) {
                x += 0.01f;

                gl.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

                //model = model.identity();
                //model.rotate(x, 1, 0, 0);
                //model.rotate(x, 0, 1, 0);
                //model.rotate(x, 0, 0, 1);

                //modelView.identity().mul(camera).mul(model);

                //mesh.easyRender(gl, modelView, projection);

                node1.getTransform().setPosition(new Vector3f(-1f, 0, 0));
                node1.getTransform().setRotation(new Quaternionf().rotationAxis(x, 0f, 1f, 0f));
                node1.getTransform().updateMatrix();

                node2.getTransform().setPosition(new Vector3f(1f, 0, 0));
                node2.getTransform().setRotation(new Quaternionf().rotationAxis(-x, 0f, 1f, 0f));
                node2.getTransform().updateMatrix();

                scene.render(gl, camera);
            }

            public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
                gl.glViewport(0, 0, width, height);
                //projection.setPerspective((float)Math.toRadians(45.0), (float)width / (float)height, 0.1f, 100.0f);
                scene.setProjection(45.0f, width, height, 0.1f, 1000.0f);
            }
        });

        animator = new Animator(this);
        animator.start();
    }
}
