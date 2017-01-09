package net.natewm.SimulatedPhysicalUsability.Graphics;

/**
 * Created by Nathan on 1/6/2017.
 */
public class GraphicsEngineMessage {
    public enum Type {
        FRAME_ENDED
    }

    private Type type;
    private Object data;

    public GraphicsEngineMessage(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
