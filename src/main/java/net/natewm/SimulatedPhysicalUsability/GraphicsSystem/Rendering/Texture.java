package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Image;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Nathan on 12/29/2016.
 */
public class Texture {
    private static final Logger LOGGER = Logger.getLogger(Texture.class.getName());

    static float anisotropy = -1f;
    int textureID;
    int width;
    int height;

    public Texture(GL3 gl, Image image) {
        fromImage(gl, image, true);
    }

    public Texture(GL3 gl, Image image, boolean quality) {
        fromImage(gl, image, quality);
    }

    public Texture(GL3 gl, ByteBuffer byteBuffer, int width, int height, boolean quality) {
        fromByteBuffer(gl, byteBuffer, width, height, quality);
    }

    public Texture(GL3 gl, ByteBuffer byteBuffer, int width, int height) {
        fromByteBuffer(gl, byteBuffer, width, height, true);
    }

    private void genTexture(GL3 gl) {
        int[] textureIDArray = {0};
        gl.glGenTextures(1, textureIDArray, 0);
        textureID = textureIDArray[0];

        gl.glBindTexture(gl.GL_TEXTURE_2D, textureID);
    }

    public void setTextureOptions(GL3 gl, int wrapS, int wrapT, int minFilter, int magFilter) {
        bind(gl);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrapS);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrapT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, magFilter);
        unbind(gl);
    }

    private void setFilters(GL3 gl, boolean quality) {
        // TODO: Allow configuring min and mag filters (Maybe in material json files)
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
        //gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
        if (quality)
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR_MIPMAP_LINEAR);
        else
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
    }

    private void setQuality(GL3 gl) {
        if (anisotropy < 0f) {
            float[] fLargest = {-1f};
            gl.glGetFloatv(gl.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, fLargest, 0);
            anisotropy = fLargest[0];
            LOGGER.log(Level.FINE, "Using ansiotropy level {0}", anisotropy);
        }

        gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);
        gl.glGenerateMipmap(gl.GL_TEXTURE_2D);
    }

    private void fromImage(GL3 gl, Image image, boolean quality) {
        // Create OpenGL texture
        genTexture(gl);

        width = image.getWidth();
        height = image.getHeight();

        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, image.getPixelFormat(), width, height, 0,
                image.getPixelFormat(), gl.GL_UNSIGNED_BYTE, image.getByteBuffer());

        setFilters(gl, quality);

        if (quality)
            setQuality(gl);
    }

    private void fromByteBuffer(GL3 gl, ByteBuffer byteBuffer, int width, int height, boolean quality) {
        genTexture(gl);

        this.width = width;
        this.height = height;

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL3.GL_RED, width, height,
                0, GL3.GL_RED, GL.GL_UNSIGNED_BYTE, byteBuffer);

        setFilters(gl, quality);

        if (quality)
            setQuality(gl);
    }

    public void updateByteBuffer(GL3 gl, ByteBuffer byteBuffer) {
        bind(gl);
        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, width, height, GL3.GL_RED, GL.GL_UNSIGNED_BYTE, byteBuffer);
        unbind(gl);
    }

    public int getTextureID() {
        return textureID;
    }

    public void bind(GL3 gl) {
        gl.glBindTexture(gl.GL_TEXTURE_2D, textureID);
    }

    public void unbind(GL3 gl) {
        gl.glBindTexture(gl.GL_TEXTURE_2D, 0);
    }

    public void dispose(GL3 gl) {
        int[] textures = {textureID};
        gl.glDeleteTextures(1, textures, 0);
        textureID = 0;
    }
}
