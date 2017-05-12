package net.natewm.SimulatedPhysicalUsability.Information;

import com.jogamp.opengl.GL;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.Transform;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

/**
 * The ground grid stores time occupied information.  It is made up of of a grid of grids, where newly visited areas
 * are added as needed to store more ground data.
 */
public class GroundGrid {
    private static final int DATA_TEXTURE_ID = 2;

    /**
     * A ground panel stores a square area of occupied data for the floor.
     */
    private class GroundPanel {
        public FloatGrid floatGrid = null;
        public MeshRenderNodeHandle meshRenderNodeHandle = null;
        public final TextureHandle textureHandle;

        /**
         * Constructor
         *
         * @param graphicsEngine Graphics engine for updating mesh in the scene
         * @param x              X position of panel in the world
         * @param y              Y position of the panel in the world
         * @param width          Width of the panel
         * @param height         Height of the panel
         * @param meshHandle     Handle to mesh node for referencing graphic in scene
         * @param materialHandle Handle to material node for updating material properties
         */
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

        /**
         * Updates the texture associated with this ground panel.
         *
         * @param graphicsEngine Graphics engine
         * @param min            Minimum value, used for stretching values between 0 and 255
         * @param max            Maximum value, used for stretching values between 0 and 255
         */
        public void updateTexture(GraphicsEngine graphicsEngine, float min, float max) {
            if (min < max - 1f)
                graphicsEngine.updateTexture(textureHandle, floatGrid.toByteBuffer(min, max));
        }

        /**
         * Disposes of the ground panel and graphics.
         *
         * @param graphicsEngine Graphics engine
         */
        public void dispose(GraphicsEngine graphicsEngine) {
            graphicsEngine.removeNodeFromRenderer(meshRenderNodeHandle);
            graphicsEngine.destroyTexture(textureHandle);
        }
    }

    private final GraphicsEngine graphicsEngine;
    private final MeshHandle meshHandle;
    private final MaterialHandle materialHandle;
    private GroundPanel[] groundPanels = null;
    private final int width;
    private final int height;
    private final int gridWidth;
    private final int gridHeight;
    private final int floorWidth;
    private final int floorHeight;
    private final float offsetX;
    private final float offsetY;
    private int updateCount = 0;
    private final int updateSkip;

    /**
     * Constructs a ground grid, which is a grid of grids for storing floor occupied time data.
     *
     * @param graphicsEngine Graphics engine
     * @param width          Width of the ground, in number of panels
     * @param height         Height of the ground, in number of panels
     * @param gridWidth      Width of panels
     * @param gridHeight     Height of panels
     * @param updateSkip     Number of panels to skip each time graphics is updated
     */
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

    /**
     * Resets ground grid to being empty.
     */
    public void reset() {
        for (GroundPanel panel : groundPanels) {
            if (panel != null) {
                panel.dispose(graphicsEngine);
            }
        }

        groundPanels = new GroundPanel[width * height];
        updateCount = 0;
    }

    /**
     * Updates the textures for the ground grid.  To speed up the process, not all textures will be updated every time
     * this is called.  Instead, a rotation is used so only a portion of the graphics needs to be updated at any given
     * time.
     */
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

    /**
     * Converts a world X position into a grid X position.
     *
     * @param x World X position
     * @return Grid X position
     */
    private int toGridX(float x) {
        return (int)Math.floor((x - offsetX) / gridWidth);
    }

    /**
     * Converts a world Y position to a grid Y position.
     *
     * @param y World Y position
     * @return Grid Y position
     */
    private int toGridY(float y) {
        return (int)Math.floor((y - offsetY) / gridHeight);
    }

    /**
     * Converts a world X position to an X position within a panel's cells.
     *
     * @param x World X position
     * @return Panel's X cell position
     */
    private int toCellX(float x) {
        return (int)(x - offsetX) % gridWidth;
    }

    /**
     * Converts a world Y position to a Y position within a panel's cells.
     *
     * @param y World Y position
     * @return Panel's Y cell position
     */
    private int toCellY(float y) {
        return (int)(y - offsetY) % gridHeight;
    }

    /**
     * Gets the panel at the given grid location.
     *
     * @param x Grid X position
     * @param y Grid Y position
     * @return Ground panel at the given position
     */
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

    /**
     * Adds a value to the data stored at a world position.
     *
     * @param position World position
     * @param value    Value to add to the value in the grid
     */
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

    /**
     * Sets a value at a given world position.
     *
     * @param position World position
     * @param value    Value to store in that world position
     */
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

    /**
     * Gets a value at a given world position.
     *
     * @param position World position
     * @return Value at that position or 0 if position is not within a ground panel
     */
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
