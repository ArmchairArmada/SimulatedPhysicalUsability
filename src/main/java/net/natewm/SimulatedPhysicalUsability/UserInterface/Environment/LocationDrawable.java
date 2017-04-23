package net.natewm.SimulatedPhysicalUsability.UserInterface.Environment;

import net.natewm.SimulatedPhysicalUsability.CollisionSystem.Rect;
import net.natewm.SimulatedPhysicalUsability.Environment.Environment;
import net.natewm.SimulatedPhysicalUsability.Environment.Location;
import net.natewm.SimulatedPhysicalUsability.Project.ProjectData;

import java.awt.*;

/**
 * Created by Nathan on 2/21/2017.
 */
public class LocationDrawable implements IEditorDrawable {
    Rect rect;
    Location location;

    public LocationDrawable(Location location) {
        this.location = location;
        rect = new Rect(location.getX(), location.getY(), 1, 1);
    }

    @Override
    public void draw(Graphics2D graphics2D, int offsetX, int offsetY, int scale) {
        graphics2D.setColor(location.getLocationType().getColor());
        graphics2D.fillOval((int)(rect.getX() * scale + offsetX+3), (int)(rect.getY() * scale + offsetY+3), scale-5, scale-5);
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public void apply(ProjectData projectData) {
        //environment.getLocations().add(location);
        projectData.addLocation(location);
    }
}
