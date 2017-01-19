package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by Nathan on 1/3/2017.
 */
public class ImageLoader implements IImageLoader {
    @Override
    public Image load(String filename) throws IOException {
        return new Image(ImageIO.read(new File(filename)));
    }
}
