package net.natewm.SimulatedPhysicalUsability.Environment;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.BinSpaceTree;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.ICollisionCollection;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MaterialHandle;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MeshHandle;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MeshRenderNodeHandle;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Geometry;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Navigation.NavigationGrid;
import net.natewm.SimulatedPhysicalUsability.Simulation.Agent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 3/14/2017.
 */
public class Environment {
    private GraphicsEngine graphicsEngine;
    GroundGrid groundGrid;
    private List<LocationType> locationTypeList = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();
    private Walls walls;
    private CollisionGrid collisionGrid;
    private ICollisionCollection<Agent> agentCollisionCollection;
    private NavigationGrid navigationGrid;
    private MeshRenderNodeHandle wallNode = null;

    public Environment(GraphicsEngine graphicsEngine) {
        this.graphicsEngine = graphicsEngine;
        groundGrid = new GroundGrid(graphicsEngine, 8, 8, 128, 128, 16);
        clear();
    }

    public GroundGrid getGroundGrid() {
        return  groundGrid;
    }

    public CollisionGrid getCollisionGrid() {
        return collisionGrid;
    }

    public Walls getWalls() {
        return walls;
    }

    public ICollisionCollection<Agent> getAgentCollisionCollection() {
        return agentCollisionCollection;
    }

    public NavigationGrid getNavigationGrid() {
        return navigationGrid;
    }

    public void clear() {
        groundGrid.reset();
        locationTypeList.clear();
        locations.clear();
        walls = new Walls();
        collisionGrid = new CollisionGrid(walls);
        navigationGrid = new NavigationGrid(collisionGrid);
        agentCollisionCollection = new BinSpaceTree<Agent>(-512, -512, 1024, 1024, 10);
        generateGraphics();
    }

    public void generateRandomEnvironment() {
        MazeGenerator mazeGenerator = new MazeGenerator(60, 60, 0.15f);
        walls = mazeGenerator.generate(); //new Walls();
        collisionGrid = new CollisionGrid(walls);
        navigationGrid = new NavigationGrid(collisionGrid);

        // ToDo: randomize this
        LocationType exit = new LocationType("exit");

        navigationGrid.addLocation(new Location(exit, -25f, -25f));
        navigationGrid.addLocation(new Location(exit, -25f, 25f));
        navigationGrid.addLocation(new Location(exit, 25f, -25f));
        navigationGrid.addLocation(new Location(exit, 25f, 25f));

        navigationGrid.addLocation(new Location(exit, 0f, -25f));
        navigationGrid.addLocation(new Location(exit, -25f, 0f));
        navigationGrid.addLocation(new Location(exit, 0f, 25f));
        navigationGrid.addLocation(new Location(exit, 25f, 0f));

        navigationGrid.generateLocationGrids();

        generateGraphics();
    }

    public void generateEnvironment() {
        collisionGrid = new CollisionGrid(walls);
        navigationGrid = new NavigationGrid(collisionGrid);

        // TODO: Remove this when location editor is ready.
        LocationType exit = new LocationType("exit");

        navigationGrid.addLocation(new Location(exit, -25f, -25f));
        navigationGrid.addLocation(new Location(exit, -25f, 25f));
        navigationGrid.addLocation(new Location(exit, 25f, -25f));
        navigationGrid.addLocation(new Location(exit, 25f, 25f));

        navigationGrid.addLocation(new Location(exit, 0f, -25f));
        navigationGrid.addLocation(new Location(exit, -25f, 0f));
        navigationGrid.addLocation(new Location(exit, 0f, 25f));
        navigationGrid.addLocation(new Location(exit, 25f, 0f));

        navigationGrid.generateLocationGrids();

        generateGraphics();
    }

    public void generateGraphics() {
        if (wallNode != null)
            graphicsEngine.removeNodeFromRenderer(wallNode);

        wallNode = new MeshRenderNodeHandle();

        Geometry geometry = walls.generateGeometry();
        MeshHandle wallMesh = new MeshHandle();
        MaterialHandle wallMaterial = new MaterialHandle();
        wallNode = new MeshRenderNodeHandle();
        try {
            graphicsEngine.loadMaterial(wallMaterial, "data/graphics/wall_material.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        graphicsEngine.createMesh(wallMesh, geometry);
        graphicsEngine.createMeshRenderNode(wallNode, wallMesh, wallMaterial);
        graphicsEngine.addNodeToRenderer(wallNode);
    }
}
