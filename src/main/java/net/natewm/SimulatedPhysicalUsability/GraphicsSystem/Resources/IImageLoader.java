package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import java.io.IOException;

/**
 * Created by Nathan on 1/3/2017.
 */
public interface IImageLoader {
    public Image load(String filename) throws IOException;
}
