package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import org.joml.Vector4f;

/**
 * Created by Nathan on 1/4/2017.
 */
public class MaterialProperty4f extends MaterialProperty {
    Vector4f value;

    public MaterialProperty4f(String name, Vector4f value) {
        super(name);
        this.value = value;
    }

    public MaterialProperty clone() {
        return new MaterialProperty4f(name, value);
    }

    public Vector4f getValue() {
        return value;
    }

    public void setValue(Vector4f value) {
        this.value = value;
    }

    @Override
    void use(GL3 gl) {
        gl.glUniform4f(uniformID, value.x, value.y, value.z, value.w);
    }
}
