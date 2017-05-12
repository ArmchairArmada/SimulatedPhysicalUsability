package net.natewm.SimulatedPhysicalUsability.Information;

import com.sun.prism.impl.BufferUtil;

import java.nio.ByteBuffer;

/**
 * Stores a grid of floating point values.
 */
public class FloatGrid {
    private final float[] data;
    private ByteBuffer byteBuffer = null;

    private final int width;
    private final int height;

    private float min;
    private float max;

    public FloatGrid(int width, int height) {
        data =  new float[width*height];
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float get(int x, int y) {
        return data[y * width + x];
    }

    public void set(int x, int y, float value) {
        data[y * width + x] = value;
    }

    public int size() {
        return data.length;
    }

    public void updateMinMax() {
        float v;
        min=Float.MAX_VALUE;
        max=Float.MIN_VALUE;

        for (float aData : data) {
            v = aData;
            if (v < min)
                min = v;
            if (v > max)
                max = v;
        }
    }

    public ByteBuffer toByteBuffer(float minValue, float maxValue) {
        float v;

        if (byteBuffer == null)
            byteBuffer = BufferUtil.newByteBuffer(data.length);

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                v = data[(height - y - 1) * width + x];
                // Scale to be from 0 to 1
                //v = Math.min(Math.max((v - minValue) / (maxValue - minValue), 0f), 1f);
                //v = (float) Math.min(Math.max(Math.log(v - minValue) / Math.log(maxValue - minValue), 0.0f), 1.0f);

                v = (float) Math.min(Math.max(
                        ((Math.log(v - minValue) / Math.log(maxValue - minValue)) +
                                ((v - minValue) / (maxValue - minValue)))
                        / 2.0
                        , 0.0f), 1.0f);

                // Convert to byte value
                byteBuffer.put((byte) (255.0f * v));
            }
        }

        byteBuffer.flip();
        return byteBuffer;
    }
}
