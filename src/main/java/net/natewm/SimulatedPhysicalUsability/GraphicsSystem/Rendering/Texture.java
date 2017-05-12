package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Image;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Texture mapping resource.
 */
public class Texture {
    private static final Logger LOGGER = Logger.getLogger(Texture.class.getName());

    private static float anisotropy = -1f;
    private int textureID;
    private int width;
    private int height;

    /**
     * Creates a texturemap from an image.
     *
     * @param gl    OpemGL
     * @param image Image to create texture from
     */
    public Texture(GL3 gl, Image image) {
        fromImage(gl, image, true);
    }

    /**
     * Creates a texturemap from an image with specified quality settings.
     *
     * If quality is True, then mipmapping will be used.
     *
     * @param gl      OpenGL
     * @param image   Image to create texture from
     * @param quality If quality filtering should be used.
     */
    public Texture(GL3 gl, Image image, boolean quality) {
        fromImage(gl, image, quality);
    }

    /**
     * Creates a texturemap from a byte buffer with quality settings.
     *
     * @param gl         OpenGL
     * @param byteBuffer Byte Buffer of grayscale image data
     * @param width      Width of the texture
     * @param height     Height of the texture
     * @param quality    Quality settings (True uses mipmapping)
     */
    public Texture(GL3 gl, ByteBuffer byteBuffer, int width, int height, boolean quality) {
        fromByteBuffer(gl, byteBuffer, width, height, quality);
    }

    /**
     * Creates a texturemap from a byte buffer.
     *
     * @param gl         OpenGL
     * @param byteBuffer Byte Buffer of grayscale image data
     * @param width      Width of the texture
     * @param height     Height of the texture
     */
    public Texture(GL3 gl, ByteBuffer byteBuffer, int width, int height) {
        fromByteBuffer(gl, byteBuffer, width, height, true);
    }

    /**
     * Generates OpenGL texture ID.
     *
     * @param gl OpenGL
     */
    private void genTexture(GL3 gl) {
        int[] textureIDArray = {0};
        gl.glGenTextures(1, textureIDArray, 0);
        textureID = textureIDArray[0];

        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
    }

    /**
     * Sets OpenGL texture options for wrapping and filters.
     *
     * @param gl        OpenGL
     * @param wrapS     Texture wrap S direction
     * @param wrapT     Texture wrap T direction
     * @param minFilter Texture min filter
     * @param magFilter Texture mag filter
     */
    public void setTextureOptions(GL3 gl, int wrapS, int wrapT, int minFilter, int magFilter) {
        bind(gl);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrapS);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrapT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, magFilter);
        unbind(gl);
    }

    /**
     * Sets the OpenGL texture filter.
     *
     * @param gl      OpenGL
     * @param quality If mapmapping should be used.
     */
    private void setFilters(GL3 gl, boolean quality) {
        // TODO: Allow configuring min and mag filters (Maybe in material json files)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        if (quality)
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        else
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    }

    /**
     * Sets texture anisotropic filtering quality
     *
     * @param gl
     */
    private void setQuality(GL3 gl) {
        if (anisotropy < 0f) {
            float[] fLargest = {-1f};
            gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, fLargest, 0);
            anisotropy = fLargest[0];
            LOGGER.log(Level.FINE, "Using anisotropy level {0}", anisotropy);
        }

        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);
        gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
    }

    /**
     * Creates the texture from an image.
     *
     * @param gl      OpenGL
     * @param image   Image to create texture from
     * @param quality Anisotropic and filter quality settings
     */
    private void fromImage(GL3 gl, Image image, boolean quality) {
        // Create OpenGL texture
        genTexture(gl);

        width = image.getWidth();
        height = image.getHeight();

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, image.getPixelFormat(), width, height, 0,
                image.getPixelFormat(), GL.GL_UNSIGNED_BYTE, image.getByteBuffer());

        setFilters(gl, quality);

        if (quality)
            setQuality(gl);
    }

    /**
     * Creates a grayscale texturemap from a byte buffer.
     *
     * @param gl         OpenGL
     * @param byteBuffer Byte buffer of grayscale texture data
     * @param width      Width of teh texture
     * @param height     Height of the texture
     * @param quality    Anisotropic and filter quality settings
     */
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

    /**
     * Updates the texture with new byte buffer data.
     *
     * @param gl         OpenGL
     * @param byteBuffer Byte buffer grayscale data
     */
    public void updateByteBuffer(GL3 gl, ByteBuffer byteBuffer) {
        bind(gl);
        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, width, height, GL3.GL_RED, GL.GL_UNSIGNED_BYTE, byteBuffer);
        unbind(gl);
    }

    /**
     * Gets the OpenGL texture ID
     *
     * @return Texture ID
     */
    public int getTextureID() {
        return textureID;
    }

    /**
     * Binds the texture so OpenGL can draw it.
     *
     * @param gl OpenGL
     */
    public void bind(GL3 gl) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
    }

    /**
     * Unbind the texture from OpenGL.
     *
     * @param gl OpenGL
     */
    public void unbind(GL3 gl) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }

    /**
     * Dispose of the texture.
     *
     * @param gl OpenGL
     */
    public void dispose(GL3 gl) {
        int[] textures = {textureID};
        gl.glDeleteTextures(1, textures, 0);
        textureID = 0;
    }
}
