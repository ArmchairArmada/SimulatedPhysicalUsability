package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

/**
 * Created by Nathan on 12/29/2016.
 */
public class Renderer {
    ArrayList<ArrayList<RenderNode>> renderGroups = new ArrayList<>();

    Matrix4f projection = new Matrix4f();
    //Matrix4f modelView = new Matrix4f();

    public Renderer() {
    }

    public int createRenderGroup() {
        renderGroups.add(new ArrayList<>());
        return renderGroups.size()-1;
    }

    public void setProjection(float fieldOfView, float width, float height, float near, float far) {
        projection.setPerspective((float)Math.toRadians(fieldOfView), width / height, near, far);
    }

    public void add(int renderGroup, RenderNode node) {
        renderGroups.get(renderGroup).add(node);
    }

    public void remove(int renderGroup, RenderNode node) {
        renderGroups.get(renderGroup).remove(node);
    }

    public void render(GL3 gl, Matrix4f camera) {
        //Vector4f v = new Vector4f();
        //Matrix4f m = new Matrix4f();
        //float r, z;
        //int c = 0;

        for (ArrayList<RenderNode> renderGroup : renderGroups) {
            if (renderGroup.size() > 0) {
                renderGroup.get(0).bind(gl);

                renderGroup.parallelStream().forEach((node) -> {
                    node.updateView(camera, projection);
                });

                for (RenderNode node : renderGroup) {

                    /*
                    modelView.set(camera).mul(node.transform.getMatrix());
                    m.set(projection).mul(modelView);
                    v.set(0f,0f,0f,1f).mul(m);

                    z = v.z;

                    v.mul(1/v.w);

                    // This was a guess based on an estimated focal length
                    // TODO: Figure out how to use camera's real focal length
                    r = 4f*node.getRadius() / z;
                    */

                    //System.out.println(z + ", " + r);

                    // TODO: Use camera's far clipping plane
                    if (node.viewZ < 1000f && node.viewZ > 0f
                            && node.viewCenter.x+node.viewRadius > -1.0f
                            && node.viewCenter.x-node.viewRadius < 1.0f
                            && node.viewCenter.y+node.viewRadius > -1.0f
                            && node.viewCenter.y-node.viewRadius < 1.0f) {
                        node.render(gl, node.modelView, projection);
                        //c++;
                    }
                }
                renderGroup.get(0).unbind(gl);
            }
        }
        //System.out.println("Draw count: " + c);
    }
}
