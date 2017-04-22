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
    //private HashMap<String, ArrayList<Location>> locations = new HashMap<>();
    private HashMap<LocationType, ArrayList<Location>> locations = new HashMap<>();
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

    public EnvironmentDescription exportEnvironment() {
        // TODO: Make sure everything gets exported and imported correctly.
        List<WallDescription> wallDescription = walls.exportWalls();
        List<LocationDescription> locationDescriptions = new ArrayList<>();
        //for (Map.Entry<String, ArrayList<Location>> entry: locations.entrySet()) {
        for (Map.Entry<LocationType, ArrayList<Location>> entry: locations.entrySet()) {
            for (Location location: entry.getValue()) {
                //locationDescriptions.add(new LocationDescription(location.getLocationType().getName(), location.getX(), location.getY()));
                locationDescriptions.add(location.makeDescription());
            }
        }

        return new EnvironmentDescription(wallDescription, locationDescriptions);
    }

    public void importEnvironment(EnvironmentDescription environmentDescription) {
        clear();
        for (WallDescription wall: environmentDescription.getWalls()) {
            walls.addWall(new Walls.Wall(wall.getStartX(), wall.getStartY(), wall.getEndX(), wall.getEndY()));
        }

        // TODO: Real location types
        LocationType tempType = new LocationType("temp", false);
        tempType.addTransition(new LocationType.Transition(tempType, 1, LocationType.SelectionMethod.RANDOM, LocationType.UnavailableBehavior.REPICK));

        for (LocationDescription loc: environmentDescription.getLocations()) {
            //locations.add(new Location(tempType, loc.getX(), loc.getY()));
            addLocation(new Location(tempType, loc.getX(), loc.getY()));
        }

        generateEnvironment();
    }

    public void addLocation(Location location) {
        /*
        if (!locations.containsKey(location.getLocationType().getName())) {
            locations.put(location.getLocationType().getName(), new ArrayList<>());
        }
        locations.get(location.getLocationType().getName()).add(location);
        */
        if (!locations.containsKey(location.getLocationType())) {
            locations.put(location.getLocationType(), new ArrayList<>());
        }
        locations.get(location.getLocationType()).add(location);
    }

    public void removeLocation(Location location) {
        // TODO: What if the location type get's renamed?
        //locations.get(location.getLocationType().getName()).remove(location);
        locations.get(location.getLocationType()).remove(location);
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

    public List<Location> getLocations() {
        ArrayList<Location> output = new ArrayList<>();
        //for (Map.Entry<String, ArrayList<Location>> entry: locations.entrySet()) {
        for (Map.Entry<LocationType, ArrayList<Location>> entry: locations.entrySet()) {
            for (Location location: entry.getValue()) {
                output.add(location);
            }
        }
        return output;
    }

    public List<Location> getLocations(String name) {
        return locations.get(name);
    }

    public Location getRandomLocation(String name) {
        List<Location> locationList = locations.get(name);
        return locationList.get((int)(Math.random()*locationList.size()));
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
        locations.clear();
        walls = new Walls();
        collisionGrid = new CollisionGrid(walls);
        navigationGrid = new NavigationGrid(collisionGrid);
        agentCollisionCollection = new BinSpaceTree<>(-512, -512, 1024, 1024, 11);
        generateGraphics();
    }

    public void generateRandomEnvironment() {
        MazeGenerator mazeGenerator = new MazeGenerator(60, 60, 0.15f);
        walls = mazeGenerator.generate(); //new Walls();
        collisionGrid = new CollisionGrid(walls);
        navigationGrid = new NavigationGrid(collisionGrid);

        // ToDo: randomize this
        LocationType entrance = new LocationType("entrance", false);
        LocationType exit = new LocationType("exit", false);

        entrance.addTransition(new LocationType.Transition(exit, 1, LocationType.SelectionMethod.RANDOM, LocationType.UnavailableBehavior.REPICK));
        exit.addTransition(new LocationType.Transition(exit, 1, LocationType.SelectionMethod.RANDOM, LocationType.UnavailableBehavior.REPICK));

        Location location;

        location = new Location(entrance, 0, 0);
        addLocation(location);

        for(int i=-25; i<25; i++) {
            location = new Location(exit, -25f, i);
            addLocation(location);
            navigationGrid.addLocation(location);

            location = new Location(exit, 25f, i+1);
            addLocation(location);
            navigationGrid.addLocation(location);

            location = new Location(exit, i, -25f);
            addLocation(location);
            navigationGrid.addLocation(location);

            location = new Location(exit, i+1, 25f);
            addLocation(location);
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

        navigationGrid.generateLocationGrids();

        generateGraphics();
    }

    public void generateEnvironment() {
        collisionGrid = new CollisionGrid(walls);
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
        for (Map.Entry<LocationType, ArrayList<Location>> entry: locations.entrySet()) {
            for (Location location: entry.getValue()) {
                navigationGrid.addLocation(location);
            }
        }

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
