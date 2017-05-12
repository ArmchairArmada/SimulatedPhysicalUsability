package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import com.jogamp.opengl.GL;
import com.sun.prism.impl.BufferUtil;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;


/**
 * Internal image class.
 */
public class Image {
    private ByteBuffer byteBuffer;  // Buffer of bytes storing pixel data
    private int pixelFormat;        // OpenGL pixel format
    private int width;              // Width of image
    private int height;             // height of image

    /**
     * Creates an image from a BufferedImage object.
     *
     * @param bufferedImage BufferedImage to create an image from.
     */
    public Image(BufferedImage bufferedImage) {
        fromBufferedImage(bufferedImage);
    }

    /**
     * Gets the byte buffer for the image.
     *
     * @return Image's byte buffer
     */
    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    /**
     * Gets the pixel format of the image (OpenGL constant).
     *
     * @return Gets the pixel format of the image
     */
    public int getPixelFormat() {
        return pixelFormat;
    }

    /**
     * Gets the width of the image.
     *
     * @return Width of the image
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the image.
     *
     * @return Height of the image
     */
    public int getHeight() {
        return height;
    }

    /**
     * Creates the image from a BufferedImage.
     *
     * @param bufferedImage BufferedImage to create image from
     */
    private void fromBufferedImage(BufferedImage bufferedImage) {
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();

        ByteBuffer dataBuffer = ByteBuffer.wrap(((DataBufferByte)bufferedImage.getData().getDataBuffer()).getData());
        dataBuffer.position(0);
        dataBuffer.mark();

        if (bufferedImage.getColorModel().hasAlpha()) {
            pixelFormat = GL.GL_RGBA;

            byteBuffer = BufferUtil.newByteBuffer(dataBuffer.capacity());

            int width = bufferedImage.getWidth();
            int span = width * 4;
            int height = bufferedImage.getHeight();
            int index;
            for (int y=0; y<bufferedImage.getHeight(); y++) {
                for (int x=0; x<width; x++) {
                    index = (height - y - 1) * span + x * 4;
                    byteBuffer.put(dataBuffer.get(index+3));
                    byteBuffer.put(dataBuffer.get(index+2));
                    byteBuffer.put(dataBuffer.get(index+1));
                    byteBuffer.put(dataBuffer.get(index));
                }
            }

            byteBuffer.flip();
        }
        else {
            pixelFormat = GL.GL_RGB;

            byteBuffer = BufferUtil.newByteBuffer(dataBuffer.capacity());

            int width = bufferedImage.getWidth();
            int span = width * 3;
            int height = bufferedImage.getHeight();
            int index;
            for (int y=0; y<bufferedImage.getHeight(); y++) {
                for (int x=0; x<width; x++) {
                    index = (height - y - 1) * span + x * 3;
                    byteBuffer.put(dataBuffer.get(index+2));
                    byteBuffer.put(dataBuffer.get(index+1));
                    byteBuffer.put(dataBuffer.get(index));
                }
            }

            byteBuffer.flip();
        }
    }
}
