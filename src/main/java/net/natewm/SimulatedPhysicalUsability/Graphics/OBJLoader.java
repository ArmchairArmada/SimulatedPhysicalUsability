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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        int pa=-1, pb=-1, pc=-1;  // Position indices
        int ta=-1, tb=-1, tc=-1;  // Texture indices
        int na=-1, nb=-1, nc=-1;  // Normal indices

        Face(String[] items) {
            String[] a, b, c;

            a = items[1].split("\\/");
            b = items[2].split("\\/");
            c = items[3].split("\\/");

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

                            if (!pointMap.containsKey(items[1])) {
                                Point p = new Point();

                                p.index = points.size();
                                p.vertex = vertices.get(face.pa);
                                if (face.na > -1)
                                    p.normal = normals.get(face.na);
                                if (face.ta > -1)
                                    p.uv = uvs.get(face.ta);

                                pointMap.put(items[1], p);
                                points.add(p);
                            }

                            if (!pointMap.containsKey(items[2])) {
                                Point p = new Point();

                                p.index = points.size();
                                p.vertex = vertices.get(face.pb);
                                if (face.nb > -1)
                                    p.normal = normals.get(face.nb);
                                if (face.tb > -1)
                                    p.uv = uvs.get(face.tb);

                                pointMap.put(items[2], p);
                                points.add(p);
                            }

                            if (!pointMap.containsKey(items[3])) {
                                Point p = new Point();

                                p.index = points.size();
                                p.vertex = vertices.get(face.pc);
                                if (face.nc > -1)
                                    p.normal = normals.get(face.nc);
                                if (face.tc > -1)
                                    p.uv = uvs.get(face.tc);

                                pointMap.put(items[3], p);
                                points.add(p);
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
