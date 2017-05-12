package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import java.io.IOException;

/**
 * Interface for loading an image from a file.
 */
public interface IImageLoader {
    Image load(String filename) throws IOException;
}
