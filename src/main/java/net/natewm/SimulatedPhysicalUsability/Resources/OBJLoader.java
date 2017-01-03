package net.natewm.SimulatedPhysicalUsability.Resources;

import net.natewm.SimulatedPhysicalUsability.Rendering.Triangle;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Created by Nathan on 12/28/2016.
 */
public class OBJLoader implements IGeometryLoader {
    private class Point {
        Vector3f vertex = null;
        Vector3f normal = null;
        Vector2f uv = null;
        int index = -1;
    }


    private class Face {
        int[] p = {-1, -1, -1};
        int[] t = {-1, -1, -1};
        int[] n = {-1, -1, -1};

        Face(String[] items) {
            String[] strings;

            for (int i=0; i<3; i++) {
                strings = items[i+1].split("\\/");

                p[i] = Integer.parseInt(strings[0])-1;

                if (strings[1].compareTo("") != 0) {
                    t[i] = Integer.parseInt(strings[1])-1;
                }

                if (strings[2].compareTo("") != 0) {
                    n[i] = Integer.parseInt(strings[2])-1;
                }
            }
        }
    }

    public Geometry load(String filename) {
        HashMap<String, Point> pointMap = new HashMap<>();;
        ArrayList<Point> points = new ArrayList<>();
        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Vector2f> uvs = new ArrayList<>();

        Geometry geometry = new Geometry();

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
                            vertices.add(new Vector3f(
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
                            Face face = new Face(items);

                            for (int i=0; i<3; i++) {
                                if (!pointMap.containsKey(items[i+1])) {
                                    Point p = new Point();

                                    p.index = points.size();
                                    p.vertex = vertices.get(face.p[i]);
                                    if (face.n[i] > -1)
                                        p.normal = normals.get(face.n[i]);
                                    if (face.t[i] > -1)
                                        p.uv = uvs.get(face.t[i]);

                                    pointMap.put(items[i+1], p);
                                    points.add(p);
                                }
                            }

                            int a = pointMap.get(items[1]).index;
                            int b = pointMap.get(items[2]).index;
                            int c = pointMap.get(items[3]).index;

                            geometry.addTriangle(new Triangle(a, b, c));
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

            for (Point point : points) {
                geometry.addVertex(point.vertex);
                if (point.normal != null)
                    geometry.addNormal(point.normal);
                if (point.uv != null)
                    geometry.addUv(point.uv);
            }

            return geometry;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
