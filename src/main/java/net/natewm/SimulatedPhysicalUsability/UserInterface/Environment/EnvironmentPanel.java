package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import javafx.util.Pair;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.BinSpaceTree;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.ICollisionCollection;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;
import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.Environment.LocationType;
import net.natewm.SimulatedPhysicalUsability.Environment.Walls;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

/**
 * Created by Nathan on 1/30/2017.
 */
public class EnvironmentPanel extends JPanel {
    public enum Tool {
        ERASER,
        WALLS,
        LOCATION
    }

    private static final int GRID_SIZE = 25;
    private static final int GRID_WIDTH = 1024;
    private static final int GRID_HEIGHT = 1024;
    private static final Color GRID_COLOR = new Color(220,220,220);

    int centerX = 0;
    int centerY = 0;
    int button = 0;
    int previousX = 0;
    int previousY = 0;
    int offsetX = 0;
    int offsetY = 0;

    private final Environment environment;
    private final ProjectData projectData;

    Tool tool = Tool.WALLS;
    private LocationType toolLocationType = null;

    ICollisionCollection<IEditorDrawable> drawables;

    public EnvironmentPanel(Environment environment, ProjectData projectData) {
        this.environment = environment;
        this.projectData = projectData;

        drawables = new BinSpaceTree<>(-GRID_WIDTH/2, -GRID_HEIGHT/2, GRID_WIDTH, GRID_HEIGHT, 10);

        setBackground(Color.white);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button = e.getButton();
                previousX = e.getX();
                previousY = e.getY();

                float x = -centerX + offsetX + e.getX();
                float y = -centerY + offsetY + e.getY();

                if (button == 1) {
                    switch (tool) {
                        case ERASER:
                            eraserTool(x, y);
                            break;

                        case WALLS:
                            wallTool(x, y);
                            break;

                        case LOCATION:
                            locationTool(x, y);
                            break;

                        default:
                            break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button = 0;
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (button == 1) {
                    float x = -centerX + offsetX + e.getX();
                    float y = -centerY + offsetY + e.getY();

                    switch (tool) {
                        case ERASER:
                            eraserTool(x, y);
                            break;

                        case WALLS:
                            wallTool(x, y);
                            break;

                        case LOCATION:
                            locationTool(x, y);
                            break;

                        default:
                            break;
                    }
                }
                else if (button == 3) {
                    offsetX -= e.getX() - previousX;
                    offsetY -= e.getY() - previousY;
                    previousX = e.getX();
                    previousY = e.getY();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        environment.generateRandomEnvironment();
        updateEnvironment();
    }


    public void clearAll() {
        drawables = new BinSpaceTree<>(-GRID_WIDTH/2, -GRID_HEIGHT/2, GRID_WIDTH, GRID_HEIGHT, 10);
        repaint();
    }

    public void updateEnvironment() {
        IEditorDrawable drawable;
        float minX;
        float minY;
        boolean horizontal;

        clearAll();

        for (Walls.Wall wall : projectData.getWalls().getWalls()) {
            minX = Math.min(wall.startX, wall.endX);
            minY = Math.min(wall.startY, wall.endY);
            horizontal = Math.abs(wall.endX-wall.startX) > Math.abs(wall.endY-wall.startY);
            drawable = new WallDrawable(minX, minY, horizontal);
            drawables.insert(drawable.getRect(), drawable);
        }

        for (Location location: projectData.getLocations()) {
            drawable = new LocationDrawable(location);
            drawables.insert(drawable.getRect(), drawable);
        }
    }


    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public void setLocationTool(LocationType locationType) {
        this.tool = Tool.LOCATION;
        toolLocationType = locationType;
    }

    private void eraserTool(float x, float y) {
        ArrayList<Pair<Rect, IEditorDrawable>> picked = new ArrayList<>();
        drawables.findOverlapping(new Rect(x/GRID_SIZE, y/GRID_SIZE, 0, 0), picked);
        for (Pair<Rect, IEditorDrawable> pair : picked) {
            drawables.remove(pair.getKey());
        }
        repaint();
    }


    private void wallTool(float x, float y) {
        float fx = (float)Math.floor((x+8)/GRID_SIZE)*GRID_SIZE;
        float fy = (float)Math.floor((y+8)/GRID_SIZE)*GRID_SIZE;

        boolean hitWall = false;

        ArrayList<Pair<Rect, IEditorDrawable>> picked = new ArrayList<>();
        drawables.findOverlapping(new Rect(x/GRID_SIZE, y/GRID_SIZE, 0, 0), picked);

        for (Pair<Rect, IEditorDrawable> pair : picked) {
            if (pair.getValue() instanceof WallDrawable) {
                hitWall = true;
                break;
            }
        }

        if (!hitWall) {
            if (new Rect(fx-8, fy+8, 18, 9).isInside(x,y)) {
                IEditorDrawable test = new WallDrawable(fx / GRID_SIZE, fy / GRID_SIZE, false);
                drawables.insert(test.getRect(), test);
                repaint();
            }
            else if (new Rect(fx+8, fy-8, 9, 18).isInside(x,y)) {
                IEditorDrawable test = new WallDrawable(fx / GRID_SIZE, fy / GRID_SIZE, true);
                drawables.insert(test.getRect(), test);
                repaint();
            }
        }
    }

    private void locationTool(float x, float y) {
        ArrayList<Pair<Rect, IEditorDrawable>> picked = new ArrayList<>();
        drawables.findOverlapping(new Rect(x/GRID_SIZE, y/GRID_SIZE, 0, 0), picked);
        if (picked.isEmpty()) {
            Location location = new Location(toolLocationType, (int) Math.floor(x / GRID_SIZE), (int) Math.floor(y / GRID_SIZE));
            IEditorDrawable test = new LocationDrawable(location);
            drawables.insert(test.getRect(), test);
            repaint();
        }
    }


    public void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;

        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        centerX = getWidth()/2;
        centerY = getHeight()/2;
        int offXMod = (centerX-offsetX) % GRID_SIZE;
        int offYMod = (centerY-offsetY) % GRID_SIZE;

        g.clearRect(0, 0, getWidth(), getHeight());

        g.setColor(GRID_COLOR);

        for (int y=0; y<getHeight(); y+=GRID_SIZE) {
            g.drawLine(0, y+offYMod, getWidth(), y+offYMod);
        }

        for (int x=0; x<getWidth(); x+=GRID_SIZE) {
            g.drawLine(x+offXMod, 0, x+offXMod, getHeight());
        }

        g.setColor(Color.lightGray);

        //int halfWidth = GRID_SIZE*(getWidth()/GRID_SIZE/2)-offsetX;
        //int halfHeight = GRID_SIZE*(getHeight()/GRID_SIZE/2)-offsetY;

        //g.drawLine(0, halfHeight, getWidth(), halfHeight);
        //g.drawLine(halfWidth, 0, halfWidth, getHeight());

        g.drawLine(0, centerY-offsetY, getWidth(), centerY-offsetY);
        g.drawLine(centerX-offsetX, 0, centerX-offsetX, getHeight());


        ArrayList<Pair<Rect, IEditorDrawable>> toDraw = new ArrayList<>();
        drawables.findOverlapping(new Rect((offsetX-centerX)/GRID_SIZE, (offsetY-centerY)/GRID_SIZE,
                ((float)getWidth())/GRID_SIZE, ((float)getHeight())/GRID_SIZE), toDraw);

        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (Pair<Rect, IEditorDrawable> pair : toDraw) {
            pair.getValue().draw(graphics2D, centerX-offsetX, centerY-offsetY, GRID_SIZE);
        }

        //g.setColor(Color.black);
        //g.fillRect(centerX-offsetX-2, centerY-offsetY-2, GRID_SIZE+5, 5);
    }


    public void applyChanges() {
        environment.clear();
        ArrayList<Pair<Rect, IEditorDrawable>> picked = new ArrayList<>();
        drawables.findOverlapping(new Rect(-512, -512, 1024, 1024), picked);
        for (Pair<Rect, IEditorDrawable> pair : picked) {
            pair.getValue().apply(projectData);
        }
        environment.generateEnvironment();
    }
}
