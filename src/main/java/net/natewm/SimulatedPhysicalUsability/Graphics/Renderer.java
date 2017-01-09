package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;

/**
 * Created by Nathan on 1/6/2017.
 */
public class Renderer {
    Scene scene;

    public Renderer() {
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public void render(GL3 gl) {
        scene.render(gl);
    }
}
