package net.natewm.SimulatedPhysicalUsability.Math;

/**
 * Created by Nathan on 12/22/2016.
 */
public class Matrix44 {
    float[] values = {
        0f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f
    };

    private static final float[] identity_values = {
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    };

    public Matrix44() {
    }

    public Matrix44(float[] values, int offset) {
        for (int i=0; i<16; i++) {
            this.values[i] = values[offset+i];
        }
    }

    public Matrix44(Matrix44 matrix44) {
        for (int i=0; i<16; i++) {
            values[i] = matrix44.values[i];
        }
    }

    public static Matrix44 identity() {
        return new Matrix44(identity_values, 0);
    }

    public static Matrix44 eulerXYZ(float x, float y, float z) {
        float cx = (float)Math.cos(x);
        //float cy = (float)Math.cos(y);
        float cz = (float)Math.cos(z);
        float sx = (float)Math.sin(x);
        float sy = (float)Math.sin(y);
        float sz = (float)Math.sin(z);

        float[] values = {
                cx*cz,          -cx*sz,         sy,     0f,
                cx*sz+sx*sy*cz, cx*cz-sx*sy*sz, -sx*cx, 0f,
                sx*sz-cx*sy*cz, sx*cz+cx*sy*sz, cx*cz,  0f,
                0f,             0f,             0f,     1f
        };

        return new Matrix44(values, 0);
    }

    public static Matrix44 angleAxis(float a, float x, float y, float z) {
        float c = (float)Math.cos(a);
        float s = (float)Math.sin(a);
        float t = (float)(1.0 - Math.cos(a));

        float[] values = {
                t*x*x+c,    t*x*y-s*z,  t*x*z+s*y,  0f,
                t*x*y+s*z,  t*y*y+c,    t*y*z-s*x,  0f,
                t*x*z-s*y,  t*y*z+s*x,  t*z*z+c,    0f,
                0f,         0f,         0f,         1f
        };

        return new Matrix44(values, 0);
    }

    public void add(Matrix44 matrix) {
        for (int i=0; i<16; i++) {
            values[i] += matrix.values[i];
        }
    }

    public void subtract(Matrix44 matrix) {
        for (int i=0; i<16; i++) {
            values[i] -= matrix.values[i];
        }
    }

    public void multiply(Matrix44 matrix) {
        float[] new_values = new float[16];
        float sum;
        for (int j=0; j<4; j++) {
            for (int i=0; i<4; i++) {
                sum = 0;
                for (int k=0; k<4; k++) {
                    sum += values[k*4+i] * matrix.values[j*4+k];
                }
                new_values[j*4+i] = sum;
            }
        }
        values = new_values;
    }

    public Vector3 multiply(Vector3 vector) {
        Vector3 new_vector = new Vector3();
        int index;
        for (int i=0; i<4; i++) {
            index = i*4;
            new_vector.values[i] = values[index]*vector.values[0] +
                    values[index + 1]*vector.values[1] +
                    values[index + 2]*vector.values[2] +
                    values[index + 3];
        }
        return new_vector;
    }
}
