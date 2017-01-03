package net.natewm.SimulatedPhysicalUsability.Resources;

/**
 * Created by Nathan on 12/29/2016.
 */

/**
 * Interface for loading geometry from a file.
 */
public interface IGeometryLoader {
    public Geometry load(String filename);
}
