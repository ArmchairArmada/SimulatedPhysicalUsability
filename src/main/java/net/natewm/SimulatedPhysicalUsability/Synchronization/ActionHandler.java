package net.natewm.SimulatedPhysicalUsability.Synchronization;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;

/**
 * Created by Nathan on 1/9/2017.
 */
public class ActionHandler {
    private Queue<IAction> actions = new LinkedList<>();

    public ActionHandler() {
    }

    public synchronized boolean isEmpty() {
        return actions.isEmpty();
    }

    public synchronized void add(IAction action) {
        actions.add(action);
    }

    private synchronized IAction remove() {
        return actions.remove();
    }

    public void processActions() {
        IAction action;
        boolean completed = false;

        while (!completed) {
            if (!isEmpty()) {
                action = remove();
                completed = action.doIt();
            }
        }
    }
}
