package net.natewm.SimulatedPhysicalUsability.Information;

import com.jogamp.opengl.GL;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Transform;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

/**
 * Created by Nathan on 1/11/2017.
 */
public class GroundGrid {
    private static final int DATA_TEXTURE_ID = 2;

    private class GroundPanel {
        public FloatGrid floatGrid = null;
        public MeshRenderNodeHandle meshRenderNodeHandle = null;
        public TextureHandle textureHandle;

        public GroundPanel(GraphicsEngine graphicsEngine, float x, float y, int width, int height, MeshHandle meshHandle, MaterialHandle materialHandle) {
            floatGrid = new FloatGrid(width, height);
            meshRenderNodeHandle = new MeshRenderNodeHandle();
            textureHandle = new TextureHandle();
            Transform transform = new Transform();
            transform.position.set(x, 0, y);

            ByteBuffer byteBuffer = floatGrid.toByteBuffer(0f, 1f);

            graphicsEngine.createMeshRenderNode(meshRenderNodeHandle, meshHandle, materialHandle);
            graphicsEngine.makeMeshNodeMaterialUnique(meshRenderNodeHandle);
            graphicsEngine.createTexture(textureHandle, byteBuffer, floatGrid.getWidth(), floatGrid.getHeight());
            graphicsEngine.setMeshNodeTexture(meshRenderNodeHandle, textureHandle, DATA_TEXTURE_ID);
            graphicsEngine.setTextureOptions(textureHandle, GL.GL_CLAMP_TO_EDGE, GL.GL_CLAMP_TO_EDGE, GL.GL_LINEAR, GL.GL_NEAREST);
            graphicsEngine.setRenderNodeTransform(meshRenderNodeHandle, transform);
            graphicsEngine.addDynamicNodeToRenderer(meshRenderNodeHandle);
        }

        public void updateTexture(GraphicsEngine graphicsEngine, float min, float max) {
            graphicsEngine.updateTexture(textureHandle, floatGrid.toByteBuffer(min, max));
        }

        public void dispose(GraphicsEngine graphicsEngine) {
            graphicsEngine.removeNodeFromRenderer(meshRenderNodeHandle);
            graphicsEngine.destroyTexture(textureHandle);
        }
    }

    //FloatGrid[] floatGrids = null;
    GraphicsEngine graphicsEngine;
    MeshHandle meshHandle;
    MaterialHandle materialHandle;
    GroundPanel[] groundPanels = null;
    int width;
    int height;
    int gridWidth;
    int gridHeight;
    int floorWidth;
    int floorHeight;
    float offsetX;
    float offsetY;
    int updateCount = 0;
    int updateSkip;

    public GroundGrid(GraphicsEngine graphicsEngine, int width,
                      int height, int gridWidth, int gridHeight, int updateSkip) {
        this.graphicsEngine = graphicsEngine;
        this.width = width;
        this.height = height;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.floorWidth = width * gridWidth;
        this.floorHeight = height * gridHeight;
        this.offsetX = -floorWidth/2.0f;
        this.offsetY = -floorHeight/2.0f;
        this.updateSkip = updateSkip;

        meshHandle = new MeshHandle();
        materialHandle = new MaterialHandle();

        try {
            graphicsEngine.loadMesh(meshHandle, materialHandle, "data/graphics/floor.json");
        } catch (Exception e) {
            e.printStackTrace();
        }

        graphicsEngine.setMaterialTextureOptions(materialHandle, 1, GL.GL_CLAMP_TO_EDGE, GL.GL_CLAMP_TO_EDGE, GL.GL_LINEAR, GL.GL_LINEAR);

        groundPanels = new GroundPanel[width * height];
    }

    public void reset() {
        for (GroundPanel panel : groundPanels) {
            if (panel != null) {
                panel.dispose(graphicsEngine);
            }
        }

        groundPanels = new GroundPanel[width * height];
        updateCount = 0;
    }

    public void update() {
        GroundPanel groundPanel;

        updateCount = (updateCount + 1) % updateSkip;

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (GroundPanel panel : groundPanels) {
            if (panel != null) {
                min = Math.min(panel.floatGrid.getMin(), min);
                max = Math.max(panel.floatGrid.getMax(), max);
            }
        }

        for (int i=0; i<groundPanels.length; i++) {
            if (i % updateSkip == updateCount) {
                groundPanel = groundPanels[i];
                if (groundPanel != null) {
                    groundPanel.floatGrid.updateMinMax();
                    groundPanel.updateTexture(graphicsEngine, min, max);
                }
            }
        }
    }

    private int toGridX(float x) {
        return (int)Math.floor((x - offsetX) / gridWidth);
    }

    private int toGridY(float y) {
        return (int)Math.floor((y - offsetY) / gridHeight);
    }

    private int toCellX(float x) {
        return (int)(x - offsetX) % gridWidth;
    }

    private int toCellY(float y) {
        return (int)(y - offsetY) % gridHeight;
    }

    private GroundPanel getPanel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return null;

        GroundPanel panel = groundPanels[y * width + x];

        if (panel == null) {
            panel = new GroundPanel(graphicsEngine, x*gridWidth+offsetX+gridWidth/2.0f, y*gridHeight+offsetY+gridHeight/2.0f, gridWidth, gridHeight, meshHandle, materialHandle);
            groundPanels[y * width + x] = panel;
        }

        return panel;
    }

    public void add(Vector3f position, float value) {
        int x = toGridX(position.x);
        int y = toGridY(position.z);

        GroundPanel panel = getPanel(x, y);

        if (panel != null) {
            int cellX = toCellX(position.x);
            int cellY = toCellY(position.z);

            panel.floatGrid.set(cellX, cellY, panel.floatGrid.get(cellX, cellY) + value);
        }
    }

    public void set(Vector3f position, float value) {
        int x = toGridX(position.x);
        int y = toGridY(position.z);

        GroundPanel panel = getPanel(x, y);

        if (panel != null) {
            int cellX = toCellX(position.x);
            int cellY = toCellY(position.z);

            panel.floatGrid.set(cellX, cellY, value);
        }
    }

    public float get(Vector3f position) {
        int x = toGridX(position.x);
        int y = toGridY(position.z);

        GroundPanel panel = getPanel(x, y);

        if (panel != null) {
            int cellX = toCellX(position.x);
            int cellY = toCellY(position.z);

            return panel.floatGrid.get(cellX, cellY);
        }

        return 0.0f;
    }
}
