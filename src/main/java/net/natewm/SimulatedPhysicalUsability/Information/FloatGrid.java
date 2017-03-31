package net.natewm.SimulatedPhysicalUsability.Information;

import com.sun.prism.impl.BufferUtil;

import java.nio.ByteBuffer;

/**
 * Created by Nathan on 1/10/2017.
 */
public class FloatGrid {
    float[] data;
    ByteBuffer byteBuffer = null;

    //FloatBuffer data;
    int width;
    int height;

    float min;
    float max;

    public FloatGrid(int width, int height) {
        data =  new float[width*height];
        this.width = width;
        this.height = height;
        //data = BufferUtil.newFloatBuffer(width*height);
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
        //return data.get(y * width + x);
    }

    public void set(int x, int y, float value) {
        data[y * width + x] = value;
        //data.put(y * width + x, value);
    }

    public int size() {
        return data.length;
        //return data.capacity();
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
                v = (float) Math.min(Math.max(Math.log(v - minValue) / Math.log(maxValue - minValue), 0.0), 1.0);
                // Convert to byte value
                byteBuffer.put((byte) (255.0f * v));
            }
        }

        byteBuffer.flip();
        return byteBuffer;
    }
}
