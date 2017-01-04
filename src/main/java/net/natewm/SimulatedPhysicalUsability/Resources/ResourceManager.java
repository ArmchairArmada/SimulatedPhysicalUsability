package net.natewm.SimulatedPhysicalUsability.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.Rendering.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

        // Load mesh description from file
        MeshDescription meshDescription = MeshDescription.loadFromJSON(objectMapper, filename);

        // Load geometry
        Geometry geometry = loadGeometry(meshDescription.getGeometry(), false);

        // Load material
        Material material = loadMaterial(meshDescription.getMaterial());

        // Construct mesh
        meshMap.put(filename, new Mesh(gl, geometry, material));
        return meshMap.get(filename);
    }


    public Material loadMaterial(String filename) throws Exception {
        if (materialMap.containsKey(filename))
            return materialMap.get(filename);

        MaterialDescription materialDescription = MaterialDescription.loadFromJSON(objectMapper, filename);
        ShaderProgram shaderProgram = loadShaderProgram(materialDescription.getVertexShader(), materialDescription.getVertexShader());

        Material material = new Material(gl, shaderProgram.getProgramID());
        for (String string : materialDescription.getProperties().getTextures()) {
            material.addTexture(gl, loadTexture(string));
        }

        // TODO: Set additional material properties

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

        vertexShaderMap.put(filename, new Shader(gl, gl.GL_VERTEX_SHADER, getLines(filename)));
        return vertexShaderMap.get(filename);
    }


    public Shader loadFragmentShader(String filename) throws Exception {
        if (vertexShaderMap.containsKey(filename))
            return fragmentShaderMap.get(filename);

        vertexShaderMap.put(filename, new Shader(gl, gl.GL_FRAGMENT_SHADER, getLines(filename)));
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


    private String[] getLines(String filename) throws IOException {
        List<String> lines;

        lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
        for (int i=0; i<lines.size(); i++) {
            lines.set(i, lines.get(i).concat("\r\n"));
        }

        return (String[]) lines.toArray();
    }
}
