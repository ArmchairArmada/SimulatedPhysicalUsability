package net.natewm.SimulatedPhysicalUsability.Information;

import com.sun.prism.impl.BufferUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Nathan on 1/10/2017.
 */
public class FloatGrid {
    float[] data;
    ByteBuffer byteBuffer = null;

    //FloatBuffer data;
    int width;
    int height;

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

    public ByteBuffer toByteBuffer() {
        if (byteBuffer == null)
            byteBuffer = BufferUtil.newByteBuffer(data.length);

        //ByteBuffer byteBuffer = BufferUtil.newByteBuffer(data.length);
        float min=Float.MAX_VALUE, max=Float.MIN_VALUE, v;

        for (int i=0; i<data.length; i++) {
            v = data[i];
            if (v < min)
                min = v;
            if (v > max)
                max = v;
        }

        for (int i=0; i<data.length; i++) {
            v = data[i];
            byteBuffer.put((byte)(1f + 253f*(v-min)/(max-min)));
        }

        byteBuffer.flip();
        return byteBuffer;
    }
}
