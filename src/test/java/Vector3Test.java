import net.natewm.SimulatedPhysicalUsability.Math.Vector3;
import org.junit.jupiter.api.*;

/**
 * Created by Nathan on 12/21/2016.
 */
public class Vector3Test {
    @Test
    public void Vector3() {
        Vector3 v = new Vector3();

        Assertions.assertEquals(v.getX(), 0f, 0.0001f);
        Assertions.assertEquals(v.getY(), 0f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 0f, 0.0001f);
    }

    @Test
    public void Vector3XYZ() {
        Vector3 v = new Vector3(1, 2, 3);

        Assertions.assertEquals(v.getX(), 1f, 0.0001f);
        Assertions.assertEquals(v.getY(), 2f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 3f, 0.0001f);
    }

    @Test
    public void Vector3Copy() {
        Vector3 v1 = new Vector3(1, 2, 3);
        Vector3 v2 = new Vector3(v1);

        Assertions.assertEquals(v1.getX(), v2.getX(), 0.0001f);
        Assertions.assertEquals(v1.getY(), v2.getY(), 0.0001f);
        Assertions.assertEquals(v1.getZ(), v2.getZ(), 0.0001f);
    }

    @Test
    public void Vector3Array() {
        float[] a = {0f, 1f, 2f, 3f, 4f};
        Vector3 v = new Vector3(a, 1);

        Assertions.assertEquals(v.getX(), 1f, 0.0001f);
        Assertions.assertEquals(v.getY(), 2f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 3f, 0.0001f);
    }

    @Test
    public void right() {
        Vector3 v = Vector3.right();

        Assertions.assertEquals(v.getX(), 1f, 0.0001f);
        Assertions.assertEquals(v.getY(), 0f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 0f, 0.0001f);
    }

    @Test
    public void up() {
        Vector3 v = Vector3.up();

        Assertions.assertEquals(v.getX(), 0f, 0.0001f);
        Assertions.assertEquals(v.getY(), 1f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 0f, 0.0001f);
    }

    @Test
    public void forward() {
        Vector3 v = Vector3.forward();

        Assertions.assertEquals(v.getX(), 0f, 0.0001f);
        Assertions.assertEquals(v.getY(), 0f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 1f, 0.0001f);
    }

    @Test
    public void setX() {
        Vector3 v = new Vector3();
        v.setX(123f);

        Assertions.assertEquals(v.getX(), 123f, 0.0001f);
    }

    @Test
    public void setY() {
        Vector3 v = new Vector3();
        v.setY(123f);

        Assertions.assertEquals(v.getY(), 123f, 0.0001f);
    }

    @Test
    public void setZ() {
        Vector3 v = new Vector3();
        v.setZ(123f);

        Assertions.assertEquals(v.getZ(), 123f, 0.0001f);
    }

    @Test
    public void setValues() {
        Vector3 v = new Vector3();
        v.setValues(1f, 2f, 3f);

        Assertions.assertEquals(v.getX(), 1f, 0.0001f);
        Assertions.assertEquals(v.getY(), 2f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 3f, 0.0001f);
    }

    @Test
    public void setFromArray() {
        float a[] = {0f, 1f, 2f, 3f};
        Vector3 v = new Vector3();
        v.setFromArray(a, 1);

        Assertions.assertEquals(v.getX(), 1f, 0.0001f);
        Assertions.assertEquals(v.getY(), 2f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 3f, 0.0001f);
    }

    @Test
    public void copyIntoArray() {
        Vector3 v = new Vector3(1f, 2f,3f);
        float a[] = new float[4];
        v.copyIntoArray(a, 1);

        Assertions.assertEquals(a[1], 1f, 0.0001f);
        Assertions.assertEquals(a[2], 2f, 0.0001f);
        Assertions.assertEquals(a[3], 3f, 0.0001f);
    }

    @Test
    public void squareMagnitude() {
        Vector3 v = new Vector3(2f, 3f, 4f);
        Assertions.assertEquals(v.squareMagnitude(), 29f, 0.0001f);
    }

    @Test
    public void magnitude() {
        Vector3 v = new Vector3(2f, 3f, 4f);
        Assertions.assertEquals(v.magnitude(), 5.38516f, 0.0001f);
    }

    @Test
    public void add() {
        Vector3 v1 = new Vector3(1f, 2f, 3f);
        Vector3 v2 = new Vector3(2f, 3f, 4f);
        v1.add(v2);

        Assertions.assertEquals(v1.getX(), 3f, 0.0001f);
        Assertions.assertEquals(v1.getY(), 5f, 0.0001f);
        Assertions.assertEquals(v1.getZ(), 7f, 0.0001f);
    }

    @Test
    public void subtract() {
        Vector3 v1 = new Vector3(1f, 2f, 3f);
        Vector3 v2 = new Vector3(2f, 3f, 4f);
        v1.subtract(v2);

        Assertions.assertEquals(v1.getX(), -1f, 0.0001f);
        Assertions.assertEquals(v1.getY(), -1f, 0.0001f);
        Assertions.assertEquals(v1.getZ(), -1f, 0.0001f);
    }

    @Test
    public void scale() {
        Vector3 v = new Vector3(1f, 2f, 3f);
        v.scale(2f);

        Assertions.assertEquals(v.getX(), 2f, 0.0001f);
        Assertions.assertEquals(v.getY(), 4f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 6f, 0.0001f);
    }

    @Test
    public void normalize() {
        Vector3 v = new Vector3(2f, 3f, 4f);
        v.normalize();

        Assertions.assertEquals(v.getX(),  0.37139f, 0.0001f);
        Assertions.assertEquals(v.getY(), 0.55709f, 0.0001f);
        Assertions.assertEquals(v.getZ(), 0.74278f, 0.0001f);
    }

    @Test
    public void dot() {
        Vector3 v1 = new Vector3(2f,3f,4f);
        Vector3 v2 = new Vector3(3f,4f,5f);

        Assertions.assertEquals(v1.dot(v2), 38f, 0.0001f);
    }

    @Test
    public void cross() {
        Vector3 v1 = new Vector3(2f,3f,4f);
        Vector3 v2 = new Vector3(3f,4f,5f);
        Vector3 v = v1.cross(v2);

        Assertions.assertEquals(v.getX(),  -1, 0.0001f);
        Assertions.assertEquals(v.getY(), 2, 0.0001f);
        Assertions.assertEquals(v.getZ(), -1, 0.0001f);
    }

    @Test
    public void angle() {
        Vector3 v1 = new Vector3(2f,3f,4f);
        Vector3 v2 = new Vector3(3f,4f,5f);

        Assertions.assertEquals(v1.angle(v2), 0.06437f, 0.0001f);
    }

    @Test
    public void parallelogramArea() {
        Vector3 v1 = new Vector3(2f,3f,4f);
        Vector3 v2 = new Vector3(3f,4f,5f);

        Assertions.assertEquals(v1.parallelogramArea(v2), 2.44949f, 0.0001f);
    }

    @Test
    public void triangleArea() {
        Vector3 v1 = new Vector3(2f,3f,4f);
        Vector3 v2 = new Vector3(3f,4f,5f);

        Assertions.assertEquals(v1.triangleArea(v2), 1.22474f, 0.0001f);
    }
}
