package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

/**
 * Created by Nathan on 12/29/2016.
 */

import java.io.IOException;

/**
 * Interface for loading geometry from a file.
 */
public interface IGeometryLoader {
    public Geometry load(String filename) throws IOException;
}
