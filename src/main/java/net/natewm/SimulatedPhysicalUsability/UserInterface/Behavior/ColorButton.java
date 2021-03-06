package net.natewm.SimulatedPhysicalUsability.UserInterface.Behavior;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nathan on 4/18/2017.
 */
public class ColorButton extends JButton {
    private Color color = Color.red;

    public ColorButton(Color c) {
        super();
        this.color = c;

        setMargin(new Insets(0, 0, 0, 0));
        setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                ((Graphics2D)g).setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g.setColor(color);
                //g.fillRect(x, y, 17, 17);
                g.fillOval(x, y,17,17);

                //g.setColor(Color.BLACK);
                //g.drawRect(x,y,32,24);
            }

            @Override
            public int getIconWidth() {
                return 16;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        });

        ColorButton self = this;
        addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(self, "Choose a Color", color);
            if (newColor != null) {
                color = newColor;
                repaint();
            }
        });
    }

    public Color getColor() {
        return color;
    }
}
