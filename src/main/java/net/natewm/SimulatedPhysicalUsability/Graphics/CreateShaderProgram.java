package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Assets.ShaderHandle;
import net.natewm.SimulatedPhysicalUsability.Assets.ShaderProgramHandle;

/**
 * Created by Nathan on 1/7/2017.
 */
public class CreateShaderProgram implements IAction {
    public CreateShaderProgram(ShaderProgramHandle shaderProgramHandle, ShaderHandle vertexShaderHandle, ShaderHandle fragmentShaderHandle) {
    }

    @Override
    public boolean doIt(GL3 gl, GraphicsEngine engine) {
        return false;
    }
}
