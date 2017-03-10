package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;

/**
 * Single float material property.
 */
public class MaterialProperty1f extends MaterialProperty {
    float value;

    /**
     * Construct the single float material property.
     *
     * @param name  Name of the property.
     * @param value Value of the property.
     */
    public MaterialProperty1f(String name, float value) {
        super(name);
        this.value = value;
    }

    /**
     * Clones this material property.
     *
     * @return Copy of this material property.
     */
    public MaterialProperty clone() {
        return new MaterialProperty1f(name, value);
    }

    /**
     * Gets the value associated with this material property.
     *
     * @return The material property's value.
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the value of the material's property.
     *
     * @param value Material property value.
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Uses this material property by setting a single float uniform in OpenGL.
     *
     * @param gl OpenGL context.
     */
    @Override
    void use(GL3 gl) {
        gl.glUniform1f(uniformID, value);
    }
}
