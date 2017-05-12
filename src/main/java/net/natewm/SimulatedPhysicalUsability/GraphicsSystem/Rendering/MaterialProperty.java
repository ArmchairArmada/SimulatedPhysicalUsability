package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;

/**
 * Material property base class.
 */
public class MaterialProperty {
    final String name;    // Name of the property
    int uniformID;  // ID of the uniform in the shader

    /**
     * Creates a material property for a given property name.
     *
     * @param name Name of the property.
     */
    public MaterialProperty(String name) {
        this.name = name;
    }

    /**
     * Copies a material property.  This will be overridden by inherited classes.
     *
     * @return Copy of the material property.
     */
    public MaterialProperty clone() {
        return null;
    }

    /**
     * Gets the name of the property.
     *
     * @return The property name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the uniform ID number used by the shader..
     *
     * @return The ID number of the uniform.
     */
    public int getUniformID() {
        return uniformID;
    }

    /**
     * Attaches the material property.
     *
     * @param gl       OpenGL context.
     * @param material Material to get the uniform location ID from.
     */
    void attach(GL3 gl, Material material) {
        uniformID = material.getShaderProgram().getUniformLocation(gl, name);
    }

    /**
     * Uses this material property.  This will be overridden.
     *
     * @param gl OpenGL context.
     */
    void use(GL3 gl) {
        // override this.
    }
}
