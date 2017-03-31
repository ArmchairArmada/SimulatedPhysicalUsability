package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import java.io.IOException;

/**
 * Interface for loading geometry from a file.
 */
public interface IGeometryLoader {
    Geometry load(String filename) throws IOException;
}
