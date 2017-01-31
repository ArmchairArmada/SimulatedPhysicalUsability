package net.natewm.SimulatedPhysicalUsability.UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Created by Nathan on 1/30/2017.
 */
public class EditorPanel extends JPanel {
    private static int GRID_SIZE = 25;
    private static Color GRID_COLOR = new Color(220,220,220);

    int button = 0;
    int previousX = 0;
    int previousY = 0;
    int offsetX = 0;
    int offsetY = 0;

    public EditorPanel() {
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
                if (button == 3) {
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

    public void paintComponent(Graphics g) {
        int centerX = getWidth()/2;
        int centerY = getHeight()/2;
        int offXMod = (centerX-offsetX) % GRID_SIZE;
        int offYMod = (centerY-offsetY) % GRID_SIZE;

        g.clearRect(0, 0, getWidth(), getHeight());

        g.setColor(GRID_COLOR   );

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

        g.setColor(Color.black);
        g.fillRect(centerX-offsetX-2, centerY-offsetY-2, GRID_SIZE+5, 5);
    }
}
