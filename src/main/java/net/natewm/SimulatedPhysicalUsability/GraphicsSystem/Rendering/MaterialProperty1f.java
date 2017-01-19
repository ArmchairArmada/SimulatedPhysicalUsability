package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;

/**
 * Created by Nathan on 1/4/2017.
 */
public class MaterialProperty1f extends MaterialProperty {
    float value;

    public MaterialProperty1f(String name, float value) {
        super(name);
        this.value = value;
    }

    public MaterialProperty clone() {
        return new MaterialProperty1f(name, value);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    void use(GL3 gl) {
        gl.glUniform1f(uniformID, value);
    }
}
