package net.natewm.SimulatedPhysicalUsability.Resources;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.sun.prism.impl.BufferUtil;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;


/**
 * Created by Nathan on 1/3/2017.
 */
public class Image {
    ByteBuffer byteBuffer;  // Buffer of bytes storing pixel data
    int pixelFormat;        // OpenGL pixel format
    int width;
    int height;

    public Image(BufferedImage bufferedImage) {
        fromBufferedImage(bufferedImage);
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public int getPixelFormat() {
        return pixelFormat;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void fromBufferedImage(BufferedImage bufferedImage) {
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();

        ByteBuffer dataBuffer = ByteBuffer.wrap(((DataBufferByte)bufferedImage.getData().getDataBuffer()).getData());
        dataBuffer.position(0);
        dataBuffer.mark();

        if (bufferedImage.getColorModel().hasAlpha()) {
            pixelFormat = GL.GL_RGBA;

            // Convert from ARGB to RGBA
            // TODO: Look into using a swizzle mask
            byteBuffer = BufferUtil.newByteBuffer(dataBuffer.capacity());

            for (int i=0; i<dataBuffer.capacity(); i+=4) {
                byteBuffer.put(dataBuffer.get(i+3));
                byteBuffer.put(dataBuffer.get(i+2));
                byteBuffer.put(dataBuffer.get(i+1));
                byteBuffer.put(dataBuffer.get(i));
            }
            byteBuffer.flip();
        }
        else {
            pixelFormat = GL.GL_RGB;

            byteBuffer = BufferUtil.newByteBuffer(dataBuffer.capacity());

            for (int i=0; i<dataBuffer.capacity(); i+=3) {
                byteBuffer.put(dataBuffer.get(i+2));
                byteBuffer.put(dataBuffer.get(i+1));
                byteBuffer.put(dataBuffer.get(i));
            }
            byteBuffer.flip();

            //byteBuffer = dataBuffer;
        }
    }
}
