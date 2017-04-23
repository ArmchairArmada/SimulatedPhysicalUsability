package net.natewm.SimulatedPhysicalUsability.Utils;

import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Nathan on 4/10/2017.
 */
public class ProbabilityChooser<T> {
    public class Item<T> {
        T object;
        int weight;

        Item(T object, int weight) {
            this.object = object;
            this.weight = weight;
        }
    }

    private ArrayList<Item<T>> items;
    private int weight_sum = 0;

    public ProbabilityChooser() {
        items = new ArrayList<>();
    }

    public Collection<T> getValues() {
        ArrayList<Object> values = new ArrayList<>();
        for (Item item : items) {
            values.add(item.object);
        }
        return (Collection<T>)values;
    }

    public void clear() {
        items.clear();
        weight_sum = 0;
    }

    public void insert(T object, int weight) {
        items.add(new Item(object, weight));
        items.sort((x,y) -> {
            return y.weight - x.weight;
        });
        weight_sum += weight;
    }

    public void remove(T object) {
        Iterator<Item<T>> iterator = items.iterator();
        Item<T> item;
        while (iterator.hasNext()) {
            item = iterator.next();
            if (item.object == object) {
                weight_sum -= item.weight;
                iterator.remove();
                return;
            }
        }
    }

    public T getRandom() {
        int value = (int)(Math.random() * weight_sum);
        int sum = 0;
        for (Item<T> item : items) {
            sum += item.weight;
            if (value < sum) {
                return item.object;
            }
        }
        return null;
    }
}
