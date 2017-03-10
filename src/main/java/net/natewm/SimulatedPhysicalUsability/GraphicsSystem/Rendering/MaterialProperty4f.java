package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;
import org.joml.Vector4f;

/**
 * Four floating point material properties.
 */
public class MaterialProperty4f extends MaterialProperty {
    Vector4f value;

    /**
     * Creates material property for four floating point values.
     *
     * @param name  Name of the property.
     * @param value Value of the property as a vector.
     */
    public MaterialProperty4f(String name, Vector4f value) {
        super(name);
        this.value = value;
    }

    /**
     * Makes a copy of this material property.
     *
     * @return Copy of this material property.
     */
    public MaterialProperty clone() {
        return new MaterialProperty4f(name, value);
    }

    /**
     * Gets the value of the material property.
     *
     * @return Material property's value.
     */
    public Vector4f getValue() {
        return value;
    }

    /**
     * Sets the value of the material property.
     *
     * @param value New value for the material property.
     */
    public void setValue(Vector4f value) {
        this.value = value;
    }

    /**
     * Uses this material property by setting a four float uniform in OpenGL.
     *
     * @param gl OpenGL context.
     */
    @Override
    void use(GL3 gl) {
        gl.glUniform4f(uniformID, value.x, value.y, value.z, value.w);
    }
}
