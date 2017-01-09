package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Assets.IAsyncHandle;
import net.natewm.SimulatedPhysicalUsability.Assets.MeshHandle;
import net.natewm.SimulatedPhysicalUsability.Rendering.Material;
import net.natewm.SimulatedPhysicalUsability.Rendering.Mesh;
import org.joml.Matrix4f;

/**
 * Created by Nathan on 1/6/2017.
 */
public class MeshSceneObject extends SceneObject {
    private MeshHandle meshHandle;
    private Mesh mesh = null;

    public MeshSceneObject(MeshHandle meshHandle) {
        super(0);
        this.meshHandle = meshHandle;
    }

    public void renderObject(GL3 gl, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        mesh.bindMatricies(gl, modelViewMatrix, projectionMatrix);
        mesh.render(gl);
    }

    @Override
    public boolean isReady() {
        if (meshHandle.isReady()) {
            mesh = meshHandle.getMesh();
            return true;
        }
        return false;
    }

    public int getMeshId() {
        return mesh.getVao();
    }

    public int getMaterialId() {
        return mesh.getMaterial().getShaderProgram().getProgramID();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Material getMaterial() {
        return mesh.getMaterial();
    }

    // TODO: Get material property
    // TODO: Set material property
}
