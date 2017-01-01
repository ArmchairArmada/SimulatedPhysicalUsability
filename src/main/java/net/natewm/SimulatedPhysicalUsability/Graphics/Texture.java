package net.natewm.SimulatedPhysicalUsability.Graphics;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created by Nathan on 12/29/2016.
 */
public class Texture {
    int textureID;

    public Texture(GL3 gl, String filename) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("data/graphics/rgb.png"));
        loadFromImage(gl, bufferedImage);
    }

    public Texture(GL3 gl, BufferedImage image) {
        loadFromImage(gl, image);
    }

    private void loadFromImage(GL3 gl, BufferedImage image) {
        // Get the data buffer from the image
        ByteBuffer dataBuffer = ByteBuffer.wrap(((DataBufferByte)image.getData().getDataBuffer()).getData());
        dataBuffer.position(0);
        dataBuffer.mark();

        // Create OpenGL texture
        IntBuffer intBuffer = Buffers.newDirectIntBuffer(1);
        gl.glGenTextures(1, intBuffer);
        textureID = intBuffer.get(0);
        gl.glBindTexture(gl.GL_TEXTURE_2D, textureID);

        if (image.getColorModel().hasAlpha()) {
            // Convert from ARGB to RGBA
            ByteBuffer byteBuffer = Buffers.newDirectByteBuffer(dataBuffer.capacity());

            for (int i=0; i<dataBuffer.capacity(); i+=4) {
                byteBuffer.put(dataBuffer.get(i+1));
                byteBuffer.put(dataBuffer.get(i+2));
                byteBuffer.put(dataBuffer.get(i+3));
                byteBuffer.put(dataBuffer.get(i));
            }
            byteBuffer.flip();

            gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_RGBA, image.getWidth(), image.getHeight(), 0, gl.GL_RGBA, gl.GL_UNSIGNED_BYTE, byteBuffer);
        }
        else {
            gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_RGB, image.getWidth(), image.getHeight(), 0, gl.GL_RGB, gl.GL_UNSIGNED_BYTE, dataBuffer);
        }

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
}
