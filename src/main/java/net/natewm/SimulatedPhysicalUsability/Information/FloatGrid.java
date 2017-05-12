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

    /**
     * Constructs a grid of floating point values
     *
     * @param width  Width of the grid
     * @param height Height of the grid
     */
    public FloatGrid(int width, int height) {
        data =  new float[width*height];
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width of the grid.
     *
     * @return Width of the grid
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the grid.
     *
     * @return Height of the grid
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the minimum value stored in the grid (may not be current).
     *
     * @return Minimum value stored in grid
     */
    public float getMin() {
        return min;
    }

    /**
     * Gets the maximum value stored in the grid (may not be current).
     *
     * @return Maximum value stored in the grid
     */
    public float getMax() {
        return max;
    }

    /**
     * Gets the value stored at a position in the grid.
     *
     * @param x X position in the grid
     * @param y Y position in the grid
     * @return Value stored in the grid position
     */
    public float get(int x, int y) {
        return data[y * width + x];
    }

    /**
     * Sets a value at a position in the grid.
     *
     * @param x     X position in the grid
     * @param y     Y position in the grid
     * @param value Value to store in the grid
     */
    public void set(int x, int y, float value) {
        data[y * width + x] = value;
    }

    /**
     * Gets the number of grid cells in the grid.
     *
     * @return Size of the grid
     */
    public int size() {
        return data.length;
    }

    /**
     * Updates the minimum and maximum values stored in the grid.
     */
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

    /**
     * Converts the float values into a byte buffer, where the values will be stretched between 0 and 255.  This uses
     * an average of the linear and log scale values so that it would have a more interesting appearance when used as
     * a heatmap texture.
     *
     * The minimum and maximum values are specified for stretching values so this grid can be used along with other
     * grids in the map, which may have different min and max values.
     *
     * @param minValue Minimum for stretching value
     * @param maxValue Maximum for stretching value
     * @return A byte buffer filled with values between 0 and 255
     */
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
