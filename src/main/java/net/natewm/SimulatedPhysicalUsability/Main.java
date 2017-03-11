package net.natewm.SimulatedPhysicalUsability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.BinSpaceTree;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.CollisionGrid;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.ICollisionCollection;
import net.natewm.SimulatedPhysicalUsability.Environment.MazeGenerator;
import net.natewm.SimulatedPhysicalUsability.Environment.Walls;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.GraphicsEngine;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MaterialHandle;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MeshHandle;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.MeshRenderNodeHandle;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources.Geometry;
import net.natewm.SimulatedPhysicalUsability.Information.GroundGrid;
import net.natewm.SimulatedPhysicalUsability.Navigation.NavigationGrid;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectDescription;
import net.natewm.SimulatedPhysicalUsability.Simulation.Agent;
import net.natewm.SimulatedPhysicalUsability.Simulation.SimulationThread;
import net.natewm.SimulatedPhysicalUsability.UserInterface.MainWindow;

import javax.swing.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.*;

/**
 * Created by Nathan on 12/21/2016.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger("net.natewm.SimulatedPhysicalUsability");

    private static final int GROUND_WIDTH = 8;
    private static final int GROUND_HEIGHT = 8;
    private static final int GROUND_GRID_WIDTH = 128;
    private static final int GROUND_GRID_HEIGHT = 128;

    public Main() {
    }

    public static void main(String[] args) {
        final String format = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS - %4$s - %2$-90s - %5$s%6$s%n";
        final String key = "java.util.logging.SimpleFormatter.format";
        System.setProperty(key, format);


        Formatter formatter = new SimpleFormatter();
        Handler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);

        LOGGER.log(Level.FINE, "Starting application.");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GraphicsEngine graphicsEngine = new GraphicsEngine();
                graphicsEngine.setRendererClearColor(new float[]{1f,1f,1f,1f});

                GroundGrid groundGrid = new GroundGrid(graphicsEngine, GROUND_WIDTH, GROUND_HEIGHT,
                        GROUND_GRID_WIDTH, GROUND_GRID_HEIGHT, GROUND_WIDTH * 2);

                //CollisionGrid collisionGrid = new CollisionGrid();
                ICollisionCollection<Agent> collisionCollection = new BinSpaceTree<Agent>(-512, 512, 1024, 1024, 10);
                //NavigationGrid navigationGrid = new NavigationGrid(collisionGrid);


                // TODO: Remove this temp garbage
                MazeGenerator mazeGenerator = new MazeGenerator(60, 60, 0.15f);
                Walls walls = mazeGenerator.generate(); //new Walls();

                Geometry geometry = walls.generateGeometry();
                MeshHandle wallMesh = new MeshHandle();
                MaterialHandle wallMaterial = new MaterialHandle();
                MeshRenderNodeHandle wallNode = new MeshRenderNodeHandle();
                try {
                    graphicsEngine.loadMaterial(wallMaterial, "data/graphics/wall_material.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                graphicsEngine.createMesh(wallMesh, geometry);
                graphicsEngine.createMeshRenderNode(wallNode, wallMesh, wallMaterial);
                graphicsEngine.addNodeToRenderer(wallNode);


                CollisionGrid collisionGrid = new CollisionGrid(walls);
                //ICollisionCollection<Agent> collisionCollection = new BinSpaceTree<>(-512, 512, 1024, 1024, 10);


                NavigationGrid navigationGrid = new NavigationGrid(collisionGrid);
                //navigationGrid.addLocation(0f, 0f);
                navigationGrid.addLocation(-25f, -25f);
                navigationGrid.addLocation(-25f, 25f);
                navigationGrid.addLocation(25f, -25f);
                navigationGrid.addLocation(25f, 25f);


                SimulationThread simulationThread = new SimulationThread(graphicsEngine, groundGrid, collisionGrid, collisionCollection, navigationGrid);
                graphicsEngine.setFrameReceiver(simulationThread.getFrameEndReciever());

                MainWindow mainWindow = new MainWindow(graphicsEngine, simulationThread, groundGrid);
            }
        });
    }
}
