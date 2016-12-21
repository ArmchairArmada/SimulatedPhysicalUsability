package net.natewm.SimulatedPhysicalUsability.Math;

/**
 * Floating point based Vector3.
 *
 * Created bv[1] Nathan on 12/21/2016.
 */
public class Vector3 {
    private float[] v = {0f, 0f, 0f};

    /**
     * Creates zero vector
     */
    public Vector3() {
        v[0] = 0f;
        v[1] = 0f;
        v[2] = 0f;
    }

    /**
     * Creates vector from x,y,z values
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public Vector3(float x, float y, float z) {
        v[0] = x;
        v[1] = y;
        v[2] = z;
    }

    /**
     * Creates vector that is a copy of a given vector
     *
     * @param vector Vector to copy
     */
    public Vector3(Vector3 vector) {
        v[0] = vector.v[0];
        v[1] = vector.v[1];
        v[2] = vector.v[2];
    }

    /**
     * Creates vector from array values
     *
     * @param array Array of float values
     * @param offset Offset into array to start copying
     */
    public Vector3(float[] array, int offset) {
        setFromArray(array, offset);
    }

    /**
     * Creates right vector (positive x)
     *
     * @return Right vector
     */
    public static Vector3 right() {
        return new Vector3(1f, 0f, 0f);
    }

    /**
     * Creates up vector (positive y)
     *
     * @return Up vector
     */
    public static Vector3 up() {
        return new Vector3(0f, 1f, 0f);
    }

    /**
     * Creates forward vector (positive z)
     *
     * @return Forward vector
     */
    public static Vector3 forward() {
        return new Vector3(0f, 0f, 1f);
    }

    /**
     * Gets x component of vector
     *
     * @return x
     */
    public float getX() {
        return v[0];
    }

    /**
     * Gets y component of vector
     *
     * @return y
     */
    public float getY() {
        return v[1];
    }

    /**
     * Gets z component of vector
     *
     * @return z
     */
    public float getZ() {
        return v[2];
    }

    /**
     * Sets x component of vector
     *
     * @param x x
     */
    public void setX(float x) {
        v[0] = x;
    }

    /**
     * Sets y component of vector
     *
     * @param y y
     */
    public void setY(float y) {
        v[1] = y;
    }

    /**
     * Sets z component of vector
     *
     * @param z z
     */
    public void setZ(float z) {
        v[2] = z;
    }

    /**
     * Sets all vector components
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void setValues(float x, float y, float z) {
        v[0] = x;
        v[1] = y;
        v[2] = z;
    }

    /**
     * Sets vector components from an array
     *
     * @param array Float array of values
     * @param offset Offset into array to start copying
     */
    public void setFromArray(float[] array, int offset) {
        v[0] = array[offset];
        v[1] = array[offset+1];
        v[2] = array[offset+2];
    }

    /**
     * Copies vector values into an array
     *
     * @param array Float array to copy into
     * @param offset Offset into array to start copying
     */
    public void copyIntoArray(float[] array, int offset) {
        array[offset] = v[0];
        array[offset+1] = v[1];
        array[offset+2] = v[2];
    }

    /**
     * Returns squared magnitude of vector
     *
     * @return Square magnitude
     */
    public float squareMagnitude() {
        return v[0]*v[0] + v[1]*v[1] + v[2]*v[2];
    }

    /**
     * Returns magnitude of vector
     *
     * @return Magnitude
     */
    public float magnitude() {
        return (float)Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
    }

    /**
     * Adds a vector into this vector (in place)
     *
     * @param vector Vector to add
     */
    public void add(Vector3 vector) {
        v[0] += vector.v[0];
        v[1] += vector.v[1];
        v[2] += vector.v[2];
    }

    /**
     * Subtracts a vector from this vector (in place)
     *
     * @param vector Vector to subtract
     */
    public void subtract(Vector3 vector) {
        v[0] -= vector.v[0];
        v[1] -= vector.v[1];
        v[2] -= vector.v[2];
    }

    /**
     * Scales this vector by some amount (in place)
     *
     * @param amount Amount to scale by
     */
    public void scale(float amount) {
        v[0] *= amount;
        v[1] *= amount;
        v[2] *= amount;
    }

    /**
     * Makes the vector unit length (in place)
     */
    public void normalize() {
        float len = magnitude();
        v[0] /= len;
        v[1] /= len;
        v[2] /= len;
    }

    /**
     * Performs dot product
     *
     * @param vector Vector to dot with
     * @return The dot product of this and given vector
     */
    public float dot(Vector3 vector) {
        return v[0]*vector.v[0] + v[1]*vector.v[1] + v[2]*vector.v[2];
    }

    /**
     * Performs cross product
     *
     * @param vector Vector to cross with
     * @return The vector result of this cross given vector
     */
    public Vector3 cross(Vector3 vector) {
        return new Vector3(
                v[1]*vector.v[2]-v[2]*vector.v[1],
                v[2]*vector.v[0]-v[0]*vector.v[2],
                v[0]*vector.v[1]-v[1]*vector.v[0]
        );
    }

    /**
     * Finds angle between this vector and another
     *
     * @param vector Vector to find angle with
     * @return Angle between this and given vector
     */
    public float angle(Vector3 vector) {
        return (float)Math.acos(dot(vector) / (magnitude() * vector.magnitude()));
    }

    /**
     * Returns area of parallelogram formed from this vector and another
     *
     * @param vector Vector to find parallelogram area with
     * @return The area of a parallelogram
     */
    public float parallelogramArea(Vector3 vector) {
        float cx = v[1]*vector.v[2]-v[2]*vector.v[1];
        float cy = v[2]*vector.v[0]-v[0]*vector.v[2];
        float cz = v[0]*vector.v[1]-v[1]*vector.v[0];

        return (float)Math.sqrt(cx*cx + cy*cy + cz*cz);
    }

    /**
     * Returns area of a triangle from from this vector and another
     *
     * @param vector Vector to find triangle area with
     * @return The area of a triangle
     */
    public float triangleArea(Vector3 vector) {
        return parallelogramArea(vector) / 2f;
    }
}
