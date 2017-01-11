package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.sun.prism.impl.BufferUtil;
import net.natewm.SimulatedPhysicalUsability.Information.FloatGrid;
import net.natewm.SimulatedPhysicalUsability.Resources.Image;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Nathan on 12/29/2016.
 */
public class Texture {
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

    public Texture(GL3 gl, FloatGrid floatGrid) {
        fromFloatGrid(gl, floatGrid, true);
    }

    public Texture(GL3 gl, FloatGrid floatGrid, boolean quality) {
        fromFloatGrid(gl, floatGrid, quality);
    }

    private void genTexture(GL3 gl) {
        int[] textureIDArray = {0};
        gl.glGenTextures(1, textureIDArray, 0);
        textureID = textureIDArray[0];

        gl.glBindTexture(gl.GL_TEXTURE_2D, textureID);
    }

    private void setFilters(GL3 gl, boolean quality) {
        // TODO: Allow configuring min and mag filters
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
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

    private void fromFloatGrid(GL3 gl, FloatGrid floatGrid, boolean quality) {
        //FloatBuffer floatBuffer = floatGrid.toFloatBuffer();
        ByteBuffer byteBuffer = floatGrid.toByteBuffer();

        genTexture(gl);

        width = floatGrid.getWidth();
        height = floatGrid.getHeight();

        //gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL3.GL_RED, width, height,
        //        0, GL3.GL_RED, GL.GL_FLOAT, floatBuffer);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL3.GL_RED, width, height,
                0, GL3.GL_RED, GL.GL_UNSIGNED_BYTE, byteBuffer);

        setFilters(gl, quality);

        if (quality)
            setQuality(gl);
    }

    public void updateFloatGrid(GL3 gl, FloatGrid floatGrid) {
        //FloatBuffer floatBuffer = floatGrid.toFloatBuffer();
        ByteBuffer byteBuffer = floatGrid.toByteBuffer();
        gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, width, height, GL3.GL_RED, GL.GL_UNSIGNED_BYTE, byteBuffer);
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
