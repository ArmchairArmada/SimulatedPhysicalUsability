package net.natewm.SimulatedPhysicalUsability.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Rendering.*;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 1/3/2017.
 */
public class ResourceManager {
    Map<String, Geometry> geometryMap = new HashMap<>();
    Map<String, Mesh> meshMap = new HashMap<>();
    Map<String, Material> materialMap = new HashMap<>();
    Map<String, Texture> textureMap = new HashMap<>();

    Map<String, Shader> vertexShaderMap = new HashMap<>();
    Map<String, Shader> fragmentShaderMap = new HashMap<>();
    Map<String, ShaderProgram> shaderProgramMap = new HashMap<>();

    IImageLoader imageLoader = new ImageLoader();
    IGeometryLoader geometryLoader = new OBJLoader();

    GL3 gl; // OpenGL
    ObjectMapper objectMapper;  // Jackson ObjectMapper for loading JSON files


    public ResourceManager(GL3 gl, ObjectMapper objectMapper) {
        this.gl = gl;
        this.objectMapper = objectMapper;
    }


    public Geometry loadGeometry(String filename, boolean keep) throws IOException {
        if (geometryMap.containsKey(filename))
            return geometryMap.get(filename);

        if (keep) {
            geometryMap.put(filename, geometryLoader.load(filename));
            return geometryMap.get(filename);
        }
        else {
            return geometryLoader.load(filename);
        }
    }


    public Mesh loadMesh(String filename) throws Exception {
        if (meshMap.containsKey(filename))
            return meshMap.get(filename);

        Path path = Paths.get(filename);
        String dir = path.getParent() + "/";

        // Load mesh description from file
        MeshDescription meshDescription = MeshDescription.loadFromJSON(objectMapper, filename);

        // Load geometry
        Geometry geometry = loadGeometry(dir + meshDescription.getGeometry(), false);

        // Load material
        Material material = loadMaterial(dir + meshDescription.getMaterial());

        // Construct mesh
        meshMap.put(filename, new Mesh(gl, geometry, material));
        return meshMap.get(filename);
    }


    public Material loadMaterial(String filename) throws Exception {
        // TODO: Figure out how to using the same material with different property values
        if (materialMap.containsKey(filename))
            return materialMap.get(filename);

        Path path = Paths.get(filename);
        String dir = path.getParent() + "/";

        MaterialDescription materialDescription = MaterialDescription.loadFromJSON(objectMapper, filename);
        ShaderProgram shaderProgram = loadShaderProgram(
                dir + materialDescription.getVertexShader(),
                dir + materialDescription.getFragmentShader());

        Material material = new Material(gl, shaderProgram);
        for (String string : materialDescription.getProperties().getTextures()) {
            material.addTexture(gl, loadTexture(dir + string));
        }

        // TODO: Set additional material properties
        // TODO: Find a cleaner way of doing this.
        // diffuseColor
        if (materialDescription.getProperties().getDiffuseColor() != null) {
            List<Double> color = materialDescription.getProperties().getDiffuseColor();
            MaterialProperty4f property = new MaterialProperty4f(
                    "diffuseColor",
                    new Vector4f(
                        color.get(0).floatValue(),
                        color.get(1).floatValue(),
                        color.get(2).floatValue(),
                        color.get(3).floatValue()
                    )
            );
            material.addProperty(gl, property);
        }

        // specularColor
        if (materialDescription.getProperties().getSpecularColor() != null) {
            List<Double> color = materialDescription.getProperties().getSpecularColor();
            MaterialProperty4f property = new MaterialProperty4f(
                    "specularColor",
                    new Vector4f(
                            color.get(0).floatValue(),
                            color.get(1).floatValue(),
                            color.get(2).floatValue(),
                            color.get(3).floatValue()
                    )
            );
            material.addProperty(gl, property);
        }

        // specularPower
        if (materialDescription.getProperties().getSpecularPower() != null) {
            double power = materialDescription.getProperties().getSpecularPower();
            MaterialProperty1f property = new MaterialProperty1f(
                    "specularPower",
                    (float) power
            );
            material.addProperty(gl, property);
        }

        // specularIntensity
        if (materialDescription.getProperties().getSpecularIntensity() != null) {
            double power = materialDescription.getProperties().getSpecularIntensity();
            MaterialProperty1f property = new MaterialProperty1f(
                    "specularIntensity",
                    (float) power
            );
            material.addProperty(gl, property);
        }

        return material;
    }


    public Texture loadTexture(String filename) throws IOException {
        if (textureMap.containsKey(filename))
            return textureMap.get(filename);

        textureMap.put(filename, new Texture(gl, imageLoader.load(filename)));
        return textureMap.get(filename);
    }


    public Shader loadVertexShader(String filename) throws Exception {
        if (vertexShaderMap.containsKey(filename))
            return vertexShaderMap.get(filename);

        vertexShaderMap.put(filename, new Shader(gl, GL3.GL_VERTEX_SHADER, getLines(filename)));
        return vertexShaderMap.get(filename);
    }


    public Shader loadFragmentShader(String filename) throws Exception {
        if (fragmentShaderMap.containsKey(filename))
            return fragmentShaderMap.get(filename);

        vertexShaderMap.put(filename, new Shader(gl, GL3.GL_FRAGMENT_SHADER, getLines(filename)));
        return vertexShaderMap.get(filename);
    }


    public ShaderProgram loadShaderProgram(String vertexFilename, String fragementFilename) throws Exception {
        String key = vertexFilename + ":" + fragementFilename;

        if (shaderProgramMap.containsKey(key))
            return shaderProgramMap.get(key);

        Shader vertexShader = loadVertexShader(vertexFilename);
        Shader fragmentShader = loadFragmentShader(fragementFilename);

        shaderProgramMap.put(key, new ShaderProgram(gl, vertexShader, fragmentShader));
        return shaderProgramMap.get(key);
    }


    public void disposeAll() {
        for (ShaderProgram shaderProgram : shaderProgramMap.values()) {
            shaderProgram.dispose(gl);
        }
        shaderProgramMap.clear();

        for (Shader shader : fragmentShaderMap.values()) {
            shader.dispose(gl);
        }
        fragmentShaderMap.clear();

        for (Shader shader : vertexShaderMap.values()) {
            shader.dispose(gl);
        }
        vertexShaderMap.clear();

        for (Texture texture : textureMap.values()) {
            texture.dispose(gl);
        }
        textureMap.clear();

        for (Mesh mesh : meshMap.values()) {
            mesh.dispose(gl);
        }
        meshMap.clear();

        geometryMap.clear();
    }


    private String[] getLines(String filename) throws IOException {
        List<String> lines;
        String[] linesArray;

        lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
        for (int i=0; i<lines.size(); i++) {
            lines.set(i, lines.get(i).concat("\r\n"));
        }

        linesArray = lines.toArray(new String[0]);
        return linesArray;
    }
}
