package net.natewm.SimulatedPhysicalUsability.Utils;

/**
 * Created by Nathan on 1/24/2017.
 */
public class ArrayUtils {
    public static void shuffle(Object[] array) {
        int i = array.length;
        int index;
        Object tmp;

        while (i > 0) {
            index = (int)(Math.random() * i);
            i--;
            tmp = array[i];
            array[i] = array[index];
            array[index] = tmp;
        }
    }
}
