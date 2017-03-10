package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import javafx.util.Pair;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.BinSpaceTree;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.ICollisionCollection;
import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;

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

    Tool tool = Tool.WALLS;

    ICollisionCollection<IEditorDrawable> drawables;

    public EnvironmentPanel() {
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
                            erasorTool(x, y);
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
                            erasorTool(x, y);
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
    }


    public void clearAll() {
        drawables = new BinSpaceTree<>(-GRID_WIDTH/2, -GRID_HEIGHT/2, GRID_WIDTH, GRID_HEIGHT, 10);
        repaint();
    }


    public void setTool(Tool tool) {
        this.tool = tool;
    }


    private void erasorTool(float x, float y) {
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
            IEditorDrawable test = new TestDrawable((int) Math.floor(x / GRID_SIZE), (int) Math.floor(y / GRID_SIZE));
            drawables.insert(test.getRect(), test);
            repaint();
        }
    }



    public void paintComponent(Graphics g) {
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

        for (Pair<Rect, IEditorDrawable> pair : toDraw) {
            pair.getValue().draw((Graphics2D)g, centerX-offsetX, centerY-offsetY, GRID_SIZE);
        }


        //g.setColor(Color.black);
        //g.fillRect(centerX-offsetX-2, centerY-offsetY-2, GRID_SIZE+5, 5);
    }
}
