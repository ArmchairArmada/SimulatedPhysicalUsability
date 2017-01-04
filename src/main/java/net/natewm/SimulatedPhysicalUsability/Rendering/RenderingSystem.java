package net.natewm.SimulatedPhysicalUsability.Rendering;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Nathan on 1/4/2017.
 */


/*
    This did not work as expected and is currently not being used.  The best I was able to accomplish was having OpenGL
    render a blank black window.

    Other files which may be removed include:
        - RenderNodeHandler
 */

public class RenderingSystem {
    private enum MessageType {
        CREATE_RENDER_GROUP,
        ADD_RENDER_NODE,
        REMOVE_RENDER_NODE,
        SET_RENDER_NODE_POSITION,
        SET_RENDER_NODE_ROTATION,
        RENDER,
        RESHAPE
    }

    private class Message {
        public MessageType type;
        public Object data;

        public Message(MessageType type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    private class RenderThread implements Runnable {
        private final GL3 gl;
        private final Renderer renderer = new Renderer();
        private final BlockingQueue<Message> queue;
        private Matrix4f cameraMatrix;
        volatile boolean running = false;

        public RenderThread(GL3 gl, BlockingQueue<Message> queue) {
            this.gl = gl;
            this.queue = queue;
        }

        @Override
        public void run() {
            renderer.init(gl);
            running = true;
            try {
                while (running) {
                    consume(queue.take());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            running = false;
        }

        private void consume(Message message) throws Exception {
            //System.out.println(message.type);

            switch (message.type) {
                case CREATE_RENDER_GROUP: {
                    renderer.createRenderGroup();
                    break;
                }

                case ADD_RENDER_NODE: {
                    int group = (Integer)((Object[])message.data)[0];
                    IRenderNode node = (IRenderNode)((Object[])message.data)[1];
                    renderer.add(group, node);
                    break;
                }

                case REMOVE_RENDER_NODE: {
                    int  group = (Integer)((Object[])message.data)[0];
                    IRenderNode node = (IRenderNode)((Object[])message.data)[1];
                    renderer.remove(group, node);
                    break;
                }

                case SET_RENDER_NODE_POSITION: {
                    IRenderNode node = (IRenderNode) ((Object[]) message.data)[0];
                    Vector3f position = (Vector3f) ((Object[]) message.data)[1];
                    node.getTransform().position = position;
                    break;
                }

                case SET_RENDER_NODE_ROTATION: {
                    IRenderNode node = (IRenderNode) ((Object[]) message.data)[0];
                    Quaternionf rotation = (Quaternionf) ((Object[]) message.data)[1];
                    node.getTransform().rotation = rotation;
                    break;
                }

                case RENDER: {
                    cameraMatrix = (Matrix4f)message.data;
                    renderer.render(gl, cameraMatrix);
                    break;
                }

                case RESHAPE: {
                    int x = (Integer)((Object[])message.data)[0];
                    int y = (Integer)((Object[])message.data)[1];
                    int width = (Integer)((Object[])message.data)[2];
                    int height = (Integer)((Object[])message.data)[3];
                    renderer.reshape(gl, x, y, width, height);
                    break;
                }

                default: {
                    throw new Exception("Unknown message type");
                }
            }
        }
    }

    private BlockingQueue<Message> messages;
    private RenderThread renderThread;
    private int renderGroups = -1;

    public RenderingSystem(GL3 gl) {
        messages = new ArrayBlockingQueue<Message>(1024);
        renderThread = new RenderThread(gl, messages);
    }

    public void start() {
        new Thread(renderThread).start();
    }

    public void stop() {
        renderThread.stop();
    }

    public int createRenderGroup() {
        try {
            messages.put(new Message(MessageType.CREATE_RENDER_GROUP, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        renderGroups++;
        return renderGroups;
    }

    public void addRenderNode(Integer renderGroup, RenderNodeHandle nodeHandle) {
        Object[] data = {
                renderGroup,
                nodeHandle.renderNode,
        };
        try {
            messages.put(new Message(MessageType.ADD_RENDER_NODE, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeRenderNode(Integer renderGroup, RenderNodeHandle nodeHandle) {
        Object[] data = {
                renderGroup,
                nodeHandle.renderNode,
        };
        try {
            messages.put(new Message(MessageType.REMOVE_RENDER_NODE, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setRenderNodePosition(RenderNodeHandle nodeHandle, Vector3f position) {
        Object[] data = {
                nodeHandle.renderNode,
                new Vector3f(position)
        };
        try {
            messages.put(new Message(MessageType.SET_RENDER_NODE_POSITION, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setRenderNodeRotation(RenderNodeHandle nodeHandle, Quaternionf rotation) {
        Object[] data = {
                nodeHandle.renderNode,
                new Quaternionf(rotation)
        };
        try {
            messages.put(new Message(MessageType.SET_RENDER_NODE_ROTATION, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void render(Matrix4f cameraMatrix) {
        try {
            messages.put(new Message(MessageType.RENDER, new Matrix4f(cameraMatrix)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void reshape(Integer x, Integer y, Integer width, Integer height) {
        Object[] data = {
                x,
                y,
                width,
                height
        };
        try {
            messages.put(new Message(MessageType.RESHAPE, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
