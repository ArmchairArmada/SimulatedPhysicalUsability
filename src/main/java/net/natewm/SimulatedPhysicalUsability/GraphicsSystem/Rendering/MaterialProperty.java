package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL3;

/**
 * Created by Nathan on 1/4/2017.
 */
public class MaterialProperty {
    String name;
    int uniformID;

    public MaterialProperty(String name) {
        this.name = name;
    }

    public MaterialProperty clone() {
        return null;
    }

    public String getName() {
        return name;
    }

    public int getUniformID() {
        return uniformID;
    }

    void attach(GL3 gl, Material material) {
        uniformID = material.getShaderProgram().getUniformLocation(gl, name);
    }

    void use(GL3 gl) {
        // override this.
    }
}
