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
import net.natewm.SimulatedPhysicalUsability.Project.EnvironmentDescription;
import net.natewm.SimulatedPhysicalUsability.Project.LocationDescription;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;
import net.natewm.SimulatedPhysicalUsability.Project.WallDescription;
import net.natewm.SimulatedPhysicalUsability.Simulation.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 3/14/2017.
 */
public class Environment {
    private GraphicsEngine graphicsEngine;
    GroundGrid groundGrid;
    private CollisionGrid collisionGrid;
    private ICollisionCollection<Agent> agentCollisionCollection;
    private NavigationGrid navigationGrid;
    private MeshRenderNodeHandle wallNode = null;
    private ProjectData projectData;
    private List<Location> entrances = new ArrayList<>();

    public Environment(GraphicsEngine graphicsEngine, ProjectData projectData) {
        this.graphicsEngine = graphicsEngine;
        this.projectData = projectData;
        groundGrid = new GroundGrid(graphicsEngine, 8, 8, 128, 128, 16);
        clear();
    }

    public GroundGrid getGroundGrid() {
        return  groundGrid;
    }

    public CollisionGrid getCollisionGrid() {
        return collisionGrid;
    }

    public ICollisionCollection<Agent> getAgentCollisionCollection() {
        return agentCollisionCollection;
    }

    public NavigationGrid getNavigationGrid() {
        return navigationGrid;
    }

    public void clear() {
        groundGrid.reset();
        //locationTypeList.clear();
        entrances.clear();
        projectData.clearEnvironment();
        collisionGrid = new CollisionGrid(projectData.getWalls());
        navigationGrid = new NavigationGrid(collisionGrid);
        agentCollisionCollection = new BinSpaceTree<>(-512, -512, 1024, 1024, 11);
        generateGraphics();
    }

    public void generateRandomEnvironment() {
        MazeGenerator mazeGenerator = new MazeGenerator(60, 60, 0.15f);
        projectData.setWalls(mazeGenerator.generate());
        collisionGrid = new CollisionGrid(projectData.getWalls());
        navigationGrid = new NavigationGrid(collisionGrid);

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

    public void generateEnvironment() {
        collisionGrid = new CollisionGrid(projectData.getWalls());
        navigationGrid = new NavigationGrid(collisionGrid);

        // TODO: Remove this when location editor is ready.
        /*
        LocationType exit = new LocationType("exit");

        for(int i=-25; i<25; i++) {
            navigationGrid.addLocation(new Location(exit, -25f, i));
            navigationGrid.addLocation(new Location(exit, 25f, i+1));

            navigationGrid.addLocation(new Location(exit, i, -25f));
            navigationGrid.addLocation(new Location(exit, i+1, 25f));
        }
        */
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

        //for (Map.Entry<String, ArrayList<Location>> entry: locations.entrySet()) {
        /*
        for (Map.Entry<LocationType, ArrayList<Location>> entry: locations.entrySet()) {
            for (Location location: entry.getValue()) {
                navigationGrid.addLocation(location);
            }
        }
        */
        for (Location location : projectData.getLocations()) {
            navigationGrid.addLocation(location);
            if (location.getLocationType().isEntrance()) {
                entrances.add(location);
            }
        }

        navigationGrid.generateLocationGrids();

        generateGraphics();
    }

    public void generateGraphics() {
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

    public Location getRandomEntrance() {
        if (!entrances.isEmpty())
            return entrances.get((int)(Math.random() * entrances.size()));
        else
            return null;
    }
}
