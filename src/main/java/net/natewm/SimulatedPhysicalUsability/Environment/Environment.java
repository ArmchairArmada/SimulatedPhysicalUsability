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
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;
import net.natewm.SimulatedPhysicalUsability.Simulation.Agent;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of the environment the simulation runs in.  This includes the heatmap, collision system, navigation, etc.
 */
public class Environment {
    private final GraphicsEngine graphicsEngine;
    private final GroundGrid groundGrid;
    private CollisionGrid collisionGrid;
    private ICollisionCollection<Agent> agentCollisionCollection;
    private NavigationGrid navigationGrid;
    private MeshRenderNodeHandle wallNode = null;
    private final ProjectData projectData;
    private final List<Location> entrances = new ArrayList<>();

    /**
     * Constructs the environment.
     *
     * @param graphicsEngine Graphics engine
     * @param projectData    Project details used for storing walls, locations, etc.
     */
    public Environment(GraphicsEngine graphicsEngine, ProjectData projectData) {
        this.graphicsEngine = graphicsEngine;
        this.projectData = projectData;
        groundGrid = new GroundGrid(graphicsEngine, 8, 8, 128, 128, 16);
        clear();
    }

    /**
     * Gets the ground grid, which is used for storing the amount of time areas are occupied by agents.
     *
     * @return The ground grid
     */
    public GroundGrid getGroundGrid() {
        return  groundGrid;
    }

    /**
     * Gets the collision grid, which is used for checking wall collisions.
     *
     * @return The collision grid
     */
    public CollisionGrid getCollisionGrid() {
        return collisionGrid;
    }

    /**
     * Gets the agent collision detection data structure.
     *
     * @return A CollisionCollection for checking agent collisions
     */
    public ICollisionCollection<Agent> getAgentCollisionCollection() {
        return agentCollisionCollection;
    }

    /**
     * Gets the navigation grid for paths leading to locations.
     *
     * @return NavigationGrid
     */
    public NavigationGrid getNavigationGrid() {
        return navigationGrid;
    }

    public void clear() {
        groundGrid.reset();
        //locationTypeList.clear();
        entrances.clear();
        projectData.clearEnvironment();
        collisionGrid = new CollisionGrid(projectData.getWalls());
        navigationGrid = new NavigationGrid(collisionGrid,0, 0, 0, 0);
        agentCollisionCollection = new BinSpaceTree<>(-512, -512, 1024, 1024, 11);
        generateGraphics();
    }

    /**
     * Generates a random environment, which consists of a maze and some locations.
     */
    public void generateRandomEnvironment() {
        MazeGenerator mazeGenerator = new MazeGenerator(60, 60, 0.15f);
        projectData.setWalls(mazeGenerator.generate());
        collisionGrid = new CollisionGrid(projectData.getWalls());
        navigationGrid = new NavigationGrid(collisionGrid, 0, 0, 0, 0);

        // ToDo: randomize this
        LocationType entrance = new LocationType("entrance", 0, 0, false, true, false);
        LocationType exit = new LocationType("exit", 0, 0, false, false, true);

        entrance.addTransition(new LocationType.Transition(exit, 1, LocationType.SelectionMethod.RANDOM, LocationType.UnavailableBehavior.REPICK));
        exit.addTransition(new LocationType.Transition(exit, 1, LocationType.SelectionMethod.RANDOM, LocationType.UnavailableBehavior.REPICK));

        projectData.addLocationType(entrance);
        projectData.addLocationType(exit);

        Location location;

        location = new Location(entrance, 0, 0);
        projectData.addLocation(location);

        for(int i=-25; i<25; i++) {
            location = new Location(exit, -25f, i);
            projectData.addLocation(location);
            navigationGrid.addLocation(location);

            location = new Location(exit, 25f, i+1);
            projectData.addLocation(location);
            navigationGrid.addLocation(location);

            location = new Location(exit, i, -25f);
            projectData.addLocation(location);
            navigationGrid.addLocation(location);

            location = new Location(exit, i+1, 25f);
            projectData.addLocation(location);
            navigationGrid.addLocation(location);
        }

        /*
        navigationGrid.addLocation(new Location(exit, -25f, -25f));
        navigationGrid.addLocation(new Location(exit, -25f, 25f));
        navigationGrid.addLocation(new Location(exit, 25f, -25f));
        navigationGrid.addLocation(new Location(exit, 25f, 25f));

        navigationGrid.addLocation(new Location(exit, 0f, -25f));
        navigationGrid.addLocation(new Location(exit, -25f, 0f));
        navigationGrid.addLocation(new Location(exit, 0f, 25f));
        navigationGrid.addLocation(new Location(exit, 25f, 0f));
        */

        for (Location loc : projectData.getLocations()) {
            navigationGrid.addLocation(loc);
            if (loc.getLocationType().isEntrance()) {
                entrances.add(loc);
            }
        }

        navigationGrid.generateLocationGrids();

        generateGraphics();
    }

    /**
     * Generates the environment using the information available in the project data.
     */
    public void generateEnvironment() {
        float minX = 0f;
        float minY = 0f;
        float maxX = 0f;
        float maxY = 0f;

        for (Location location : projectData.getLocations()) {
            minX = Math.min(minX, location.getX());
            minY = Math.min(minY, location.getY());
            maxX = Math.max(maxX, location.getX());
            maxY = Math.max(maxY, location.getY());
        }

        collisionGrid = new CollisionGrid(projectData.getWalls());
        navigationGrid = new NavigationGrid(collisionGrid, minX-5, minY-5, maxX+5, maxY+5);

        for (Location location : projectData.getLocations()) {
            navigationGrid.addLocation(location);
            if (location.getLocationType().isEntrance()) {
                entrances.add(location);
            }
        }

        navigationGrid.generateLocationGrids();

        generateGraphics();
    }

    /**
     * Generates the graphics for the environment (walls).
     */
    private void generateGraphics() {
        if (wallNode != null)
            graphicsEngine.removeNodeFromRenderer(wallNode);

        wallNode = new MeshRenderNodeHandle();

        Geometry geometry = projectData.getWalls().generateGeometry();
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

    /**
     * Gets a random entrance location where agents can enter the environment.
     *
     * @return A location that is an entrance
     */
    public Location getRandomEntrance() {
        if (!entrances.isEmpty())
            return entrances.get((int)(Math.random() * entrances.size()));
        else
            return null;
    }
}
