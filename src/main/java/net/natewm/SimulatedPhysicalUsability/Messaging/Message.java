package net.natewm.SimulatedPhysicalUsability.Messaging;

/**
 * Created by Nathan on 4/11/2017.
 */
public class Message {
    public enum MsgType {
        SIM_PLAY,               // TODO: Simulation played.
        SIM_PAUSE,              // TODO: Simulation paused.
        SIM_STOP,               // TODO: Simulation stopped.
        ENV_ADD_LOCTYPE,        // TODO: When LocationType is created.
        ENV_REMOVE_LOCTYPE,     // TODO: When LocationType is removed.
        ENV_ADD_LOCATION,       // TODO: When a location is added.
        ENV_REMOVE_LOCATION,    // TODO: When a location is removed.
        ENV_ADD_WALL,           // TODO: When a wall is added.
        ENV_REMOVE_WALL,        // TODO: When a wall is removed.
        AGENT_ENTERED,          // TODO: When an agent is created.
        AGENT_ARRIVED,          // TODO: When an agent arrives at a destination location.
        AGENT_EXITED,           // TODO: When an agent is destroyed.
        NAV_GEN_BEGAN,          // TODO: Navigation grids began generating.
        NAV_GAN_COMPLETED,      // TODO: Navigation grid completed generating.
        DATA_GRID_POINT         // TODO: Adds a value to the data grid.
    }

    Object data;
    MsgType msgType;

    public Message(MsgType msgType, Object data) {
        this.data = data;
        this.msgType = msgType;
    }

    public Object getData() {
        return data;
    }

    public MsgType getMsgType() {
        return msgType;
    }
}
