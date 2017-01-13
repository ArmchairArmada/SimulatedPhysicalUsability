package net.natewm.SimulatedPhysicalUsability.Simulation;

import com.jogamp.opengl.GL;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.Information.FloatGrid;
import net.natewm.SimulatedPhysicalUsability.Rendering.Material;
import net.natewm.SimulatedPhysicalUsability.Rendering.Mesh;
import net.natewm.SimulatedPhysicalUsability.Rendering.MeshRenderNode;
import net.natewm.SimulatedPhysicalUsability.Rendering.Transform;
import org.joml.Vector3f;

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

            graphicsEngine.createMeshRenderNode(meshRenderNodeHandle, meshHandle, materialHandle);
            graphicsEngine.makeMeshNodeMaterialUnique(meshRenderNodeHandle);
            graphicsEngine.createTexture(textureHandle, floatGrid);
            graphicsEngine.setMeshNodeTexture(meshRenderNodeHandle, textureHandle, DATA_TEXTURE_ID);
            graphicsEngine.setTextureOptions(textureHandle, GL.GL_CLAMP_TO_EDGE, GL.GL_CLAMP_TO_EDGE, GL.GL_LINEAR, GL.GL_NEAREST);
            graphicsEngine.setRenderNodeTransform(meshRenderNodeHandle, transform);
            graphicsEngine.addDynamicNodeToRenderer(meshRenderNodeHandle);
        }

        public void updateTexture(GraphicsEngine graphicsEngine) {
            graphicsEngine.updateTexture(textureHandle, floatGrid);
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

    public GroundGrid(GraphicsEngine graphicsEngine, MeshHandle meshHandle, MaterialHandle materialHandle, int width, int height, int gridWidth, int gridHeight) {
        this.graphicsEngine = graphicsEngine;
        this.meshHandle = meshHandle;
        this.materialHandle = materialHandle;
        this.width = width;
        this.height = height;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.floorWidth = width * gridWidth;
        this.floorHeight = height * gridHeight;
        this.offsetX = -floorWidth/2.0f;
        this.offsetY = -floorHeight/2.0f;

        groundPanels = new GroundPanel[width * height];
    }

    public void update() {
        GroundPanel groundPanel;
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                if ((x+y) % width == updateCount % width) {
                    groundPanel = groundPanels[y * width + x];
                    if (groundPanel != null) {
                        groundPanel.updateTexture(graphicsEngine);
                    }
                }
            }
        }
        updateCount++;
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
