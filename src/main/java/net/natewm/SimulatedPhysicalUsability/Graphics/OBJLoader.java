package net.natewm.SimulatedPhysicalUsability.Graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Nathan on 12/28/2016.
 */
public class OBJLoader implements IGeometryLoader {

    private class Face {
        int pa, pb, pc;  // Position indices
        int ta, tb, tc;  // Texture indices
        int na, nb, nc;  // Normal indices

        Face(String[] items, boolean quad) {
            String[] a, b, c;

            a = items[1].split("\\/");
            if (quad) {
                b = items[3].split("\\/");
                c = items[4].split("\\/");
            }
            else {
                b = items[2].split("\\/");
                c = items[3].split("\\/");
            }

            pa = Integer.parseInt(a[0])-1;
            pb = Integer.parseInt(b[0])-1;
            pc = Integer.parseInt(c[0])-1;

            if (a[1].compareTo("") != 0) {
                ta = Integer.parseInt(a[1])-1;
                tb = Integer.parseInt(b[1])-1;
                tc = Integer.parseInt(c[1])-1;
            }

            if (a[2].compareTo("") != 0) {
                na = Integer.parseInt(a[2])-1;
                nb = Integer.parseInt(b[2])-1;
                nc = Integer.parseInt(c[2])-1;
            }
        }
    }

    /**
     * This is a limited implementation of OBJ file loading.  Some features are purposely omitted.
     *
     * Limitations include:
     *  - Face vertices, textures, and normals all use same index
     *  - Only one object can be described in the file (o is ignored).
     *  - All meshes are smoothed (no smoothing groups)
     *  - No per-vertex colors (general limitation of OBJ)
     *  - Materials are not loaded (must be loaded separately)
     *
     * @param filename
     * @return
     */
    public Geometry load(String filename) {
        Geometry geometry = new Geometry();
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Vector2f> uvs = new ArrayList<>();

        final ArrayList<Vector3f> newNormals = new ArrayList<>();
        final ArrayList<Vector2f> newUvs = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach((String line) -> {
                if (!line.startsWith("#")) {
                    String[] items = line.split("\\s+");

                    switch(items[0]) {
                        // Per-vertex color does not seem supported

                        case "o":   // Object name
                            // Ignore
                            break;

                        case "v":   // Vertex
                            geometry.addVertex(new Vector3f(
                                Float.parseFloat(items[1]),
                                Float.parseFloat(items[2]),
                                Float.parseFloat(items[3])
                            ));
                            break;

                        case "vn":  // Normal
                            normals.add(new Vector3f(
                                Float.parseFloat(items[1]),
                                Float.parseFloat(items[2]),
                                Float.parseFloat(items[3])
                            ));

                            break;

                        case "vt":  // UV
                            uvs.add(new Vector2f(
                                Float.parseFloat(items[1]),
                                Float.parseFloat(items[2])
                            ));
                            break;

                        case "f":   // Face
                            while (newNormals.size() < geometry.vertexCount())
                                newNormals.add(null);

                            while (newUvs.size() < geometry.vertexCount())
                                newUvs.add(null);

                            Face face = new Face(items, false);

                            geometry.addTriangle(new Triangle(face.pa, face.pb, face.pc));

                            newNormals.set(face.pa, normals.get(face.na));
                            newNormals.set(face.pb, normals.get(face.nb));
                            newNormals.set(face.pc, normals.get(face.nc));

                            newUvs.set(face.pa, uvs.get(face.ta));
                            newUvs.set(face.pb, uvs.get(face.tb));
                            newUvs.set(face.pc, uvs.get(face.tc));

                            // Split quads into triangles
                            if (items.length > 4) {
                                face = new Face(items, true);

                                geometry.addTriangle(new Triangle(face.pa, face.pb, face.pc));

                                newNormals.set(face.pa, normals.get(face.na));
                                newNormals.set(face.pb, normals.get(face.nb));
                                newNormals.set(face.pc, normals.get(face.nc));

                                newUvs.set(face.pa, uvs.get(face.ta));
                                newUvs.set(face.pb, uvs.get(face.tb));
                                newUvs.set(face.pc, uvs.get(face.tc));
                            }
                            break;

                        case "s":   // Smoothing
                            // Ignore
                            break;

                        case "mtllib":  // Material file
                            // TODO:  Load material?
                            // Materials currently ignored and should be loaded separately.
                            break;

                        case "usemtl":  // Material name
                            // TODO: Load material?
                            break;

                        default:
                            break;
                    }
                }

            });

            if (normals.size() > 0)
                for (Vector3f vector: newNormals)
                    geometry.addNormal(vector);

            if (uvs.size() > 0)
                for (Vector2f vector: newUvs)
                    geometry.addUv(vector);

            return geometry;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
