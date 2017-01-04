package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Resources.Image;

import java.nio.IntBuffer;

/**
 * Created by Nathan on 12/29/2016.
 */
public class Texture {
    int textureID;

    public Texture(GL3 gl, Image image) {
        fromImage(gl, image);
    }

    private void fromImage(GL3 gl, Image image) {
        // Create OpenGL texture
        IntBuffer intBuffer = Buffers.newDirectIntBuffer(1);
        gl.glGenTextures(1, intBuffer);
        textureID = intBuffer.get(0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, textureID);

        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, image.getPixelFormat(), image.getWidth(), image.getHeight(), 0,
                image.getPixelFormat(), gl.GL_UNSIGNED_BYTE, image.getByteBuffer());

        // TODO: Allow configuring min and mag filters
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR_MIPMAP_LINEAR);

        float[] fLargest = {-1f};
        gl.glGetFloatv(gl.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, fLargest, 0);

        gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAX_ANISOTROPY_EXT, fLargest[0]);
        gl.glGenerateMipmap(gl.GL_TEXTURE_2D);
    }

    public int getTextureID() {
        return textureID;
    }

    public void use(GL3 gl) {
        gl.glBindTexture(gl.GL_TEXTURE_2D, textureID);
    }

    public void dispose(GL3 gl) {
        int[] textures = {textureID};
        gl.glDeleteTextures(1, textures, 0);
        textureID = 0;
    }
}
