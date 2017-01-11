package net.natewm.SimulatedPhysicalUsability.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.Rendering.*;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

/**
 * Created by Nathan on 1/3/2017.
 */
public class ResourceManager {
    private static final Logger LOGGER = Logger.getLogger(ResourceManager.class.getName());

    Map<String, Geometry> geometryMap = new HashMap<>();
    Map<String, MeshHandle> meshMap = new HashMap<>();
    Map<String, MaterialHandle> materialMap = new HashMap<>();
    Map<String, TextureHandle> textureMap = new HashMap<>();

    Map<String, ShaderHandle> vertexShaderMap = new HashMap<>();
    Map<String, ShaderHandle> fragmentShaderMap = new HashMap<>();
    Map<String, ShaderProgramHandle> shaderProgramMap = new HashMap<>();

    IImageLoader imageLoader = new ImageLoader();
    IGeometryLoader geometryLoader = new OBJLoader();

    //GL3 gl; // OpenGL
    ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper for loading JSON files
    GraphicsEngine graphicsEngine;


    //public ResourceManager(GL3 gl, ObjectMapper objectMapper) {
    public ResourceManager(GraphicsEngine graphicsEngine) {
        //this.gl = gl;
        //this.objectMapper = objectMapper;
        this.graphicsEngine = graphicsEngine;
    }


    public Geometry loadGeometry(String filename, boolean keep) throws IOException {
        if (geometryMap.containsKey(filename))
            return geometryMap.get(filename);

        LOGGER.log(Level.FINE, "Loading Geometry: {0}", filename);

        if (keep) {
            geometryMap.put(filename, geometryLoader.load(filename));
            return geometryMap.get(filename);
        }
        else {
            return geometryLoader.load(filename);
        }
    }


    public void loadMesh(MeshHandle meshHandle, String filename) throws Exception {
        if (meshMap.containsKey(filename)) {
            //return meshMap.get(filename);
            MeshHandle handle = meshMap.get(filename);
            while (handle.getMesh() == null) {
                sleep(1);
            }
            meshHandle.set(handle);
        }

        LOGGER.log(Level.FINE, "Loading Mesh: {0}", filename);

        Path path = Paths.get(filename);
        String dir = path.getParent() + "/";

        // Load mesh description from file
        MeshDescription meshDescription = MeshDescription.loadFromJSON(objectMapper, filename);

        // Load geometry
        Geometry geometry = new Geometry();

        for (String geometryFile : meshDescription.getGeometry()) {
            Geometry tmpGeometry = loadGeometry(dir + geometryFile, false);
            geometry.appendGeometry(tmpGeometry);
        }

        //if (meshDescription.getGeometryLowRes() != null) {
        //    Geometry geometryLowRes = loadGeometry(dir + meshDescription.getGeometryLowRes(), false);
        //    geometry.appendGeometry(geometryLowRes);
        //}

        // Load material
        //Material material = loadMaterial(dir + meshDescription.getMaterial());
        MaterialHandle materialHandle = new MaterialHandle();
        loadMaterial(materialHandle, dir + meshDescription.getMaterial());

        // Construct mesh
        //meshMap.put(filename, new Mesh(gl, geometry, material));
        meshMap.put(filename, meshHandle);
        graphicsEngine.createMesh(meshHandle, geometry, materialHandle);
        //return meshMap.get(filename);
    }


    public void loadMaterial(MaterialHandle materialHandle, String filename) throws Exception {
        // TODO: Figure out how to using the same material with different property values
        if (materialMap.containsKey(filename)) {
            //return materialMap.get(filename);
            MaterialHandle handle = materialMap.get(filename);
            while (handle.getMaterial() == null) {
                sleep(1);
            }
            materialHandle.set(handle);
        }

        LOGGER.log(Level.FINE, "Loading Material: {0}", filename);

        Path path = Paths.get(filename);
        String dir = path.getParent() + "/";

        MaterialDescription materialDescription = MaterialDescription.loadFromJSON(objectMapper, filename);
        ShaderProgramHandle shaderProgramHandle = new ShaderProgramHandle();

        loadShaderProgram(shaderProgramHandle,
                dir + materialDescription.getVertexShader(),
                dir + materialDescription.getFragmentShader()
        );

        //ShaderProgram shaderProgram = loadShaderProgram(
        //        dir + materialDescription.getVertexShader(),
        //        dir + materialDescription.getFragmentShader());

        //Material material = new Material(gl, shaderProgram);
        MaterialHandle materialHandle1 = new MaterialHandle();
        List<TextureHandle> textureHandles = new ArrayList<>();

        for (String string : materialDescription.getProperties().getTextures()) {
            //material.addTexture(gl, loadTexture(dir + string));
            TextureHandle textureHandle = new TextureHandle();
            textureHandles.add(textureHandle);
            loadTexture(textureHandle, dir + string);
        }

        List<MaterialPropertyHandle> materialPropertyHandles = new ArrayList<>();

        // TODO: Set additional material properties
        // TODO: Find a cleaner way of doing this.
        // diffuseColor
        if (materialDescription.getProperties().getDiffuseColor() != null) {
            List<Double> color = materialDescription.getProperties().getDiffuseColor();
            /*
            MaterialProperty4f property = new MaterialProperty4f(
                    "diffuseColor",
                    new Vector4f(
                        color.get(0).floatValue(),
                        color.get(1).floatValue(),
                        color.get(2).floatValue(),
                        color.get(3).floatValue()
                    )
            );
            */

            MaterialPropertyHandle materialPropertyHandle = new MaterialPropertyHandle();
            graphicsEngine.createMaterialProperty4f(materialPropertyHandle,
                    "diffuseColor",
                    new Vector4f(
                            color.get(0).floatValue(),
                            color.get(1).floatValue(),
                            color.get(2).floatValue(),
                            color.get(3).floatValue()
                    )
            );

            materialPropertyHandles.add(materialPropertyHandle);

            //material.addProperty(gl, property);
        }

        // specularColor
        if (materialDescription.getProperties().getSpecularColor() != null) {
            List<Double> color = materialDescription.getProperties().getSpecularColor();
            /*
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
            */
            MaterialPropertyHandle materialPropertyHandle = new MaterialPropertyHandle();
            graphicsEngine.createMaterialProperty4f(materialPropertyHandle,
                    "specularColor",
                    new Vector4f(
                            color.get(0).floatValue(),
                            color.get(1).floatValue(),
                            color.get(2).floatValue(),
                            color.get(3).floatValue()
                    )
            );

            materialPropertyHandles.add(materialPropertyHandle);
        }

        // specularPower
        if (materialDescription.getProperties().getSpecularPower() != null) {
            double power = materialDescription.getProperties().getSpecularPower();
            /*
            MaterialProperty1f property = new MaterialProperty1f(
                    "specularPower",
                    (float) power
            );
            material.addProperty(gl, property);
            */
            MaterialPropertyHandle materialPropertyHandle = new MaterialPropertyHandle();
            graphicsEngine.createMaterialProperty1f(materialPropertyHandle,
                    "specularPower",
                    (float)power
            );

            materialPropertyHandles.add(materialPropertyHandle);
        }

        // specularIntensity
        if (materialDescription.getProperties().getSpecularIntensity() != null) {
            double intensity = materialDescription.getProperties().getSpecularIntensity();
            /*
            MaterialProperty1f property = new MaterialProperty1f(
                    "specularIntensity",
                    (float) power
            );
            material.addProperty(gl, property);
            */

            MaterialPropertyHandle materialPropertyHandle = new MaterialPropertyHandle();
            graphicsEngine.createMaterialProperty1f(materialPropertyHandle,
                    "specularIntensity",
                    (float) intensity
            );

            materialPropertyHandles.add(materialPropertyHandle);
        }

        //return material;
        graphicsEngine.createMaterial(materialHandle, shaderProgramHandle,
                textureHandles, materialPropertyHandles);
    }


    public void loadTexture(TextureHandle textureHandle, String filename) throws Exception {
        if (textureMap.containsKey(filename)) {
            TextureHandle handle = textureMap.get(filename);
            while (handle.getTexture() == null) {
                sleep(1);
            }
            //return textureMap.get(filename);
            textureHandle.set(handle);
        }

        LOGGER.log(Level.FINE, "Loading Texture: {0}", filename);

        //textureMap.put(filename, new Texture(gl, imageLoader.load(filename)));
        textureMap.put(filename, textureHandle);
        graphicsEngine.createTexture(textureHandle, imageLoader.load(filename));

        //return textureMap.get(filename);
    }


    public void loadVertexShader(ShaderHandle shaderHandle, String filename) throws Exception {
        if (vertexShaderMap.containsKey(filename)) {
            ShaderHandle handle = vertexShaderMap.get(filename);
            while (handle.getShader() == null) {
                sleep(1);
            }
            //return vertexShaderMap.get(filename);
            shaderHandle.set(handle);
        }

        LOGGER.log(Level.FINE, "Loading Vertex Shader: {0}", filename);

        //vertexShaderMap.put(filename, new Shader(gl, gl.GL_VERTEX_SHADER, getLines(filename)));
        graphicsEngine.createShader(shaderHandle, GL3.GL_VERTEX_SHADER, getLines(filename));
        //return vertexShaderMap.get(filename);
    }


    public void loadFragmentShader(ShaderHandle shaderHandle, String filename) throws Exception {
        if (fragmentShaderMap.containsKey(filename)) {
            ShaderHandle handle = fragmentShaderMap.get(filename);
            while (handle.getShader() == null) {
                sleep(1);
            }
            //return fragmentShaderMap.get(filename);
            shaderHandle.set(handle);
        }

        LOGGER.log(Level.FINE, "Loading Fragment Shader: {0}", filename);

        //vertexShaderMap.put(filename, new Shader(gl, gl.GL_FRAGMENT_SHADER, getLines(filename)));
        graphicsEngine.createShader(shaderHandle, GL3.GL_FRAGMENT_SHADER, getLines(filename));
        //return vertexShaderMap.get(filename);
    }


    public void loadShaderProgram(ShaderProgramHandle shaderProgramHandle, String vertexFilename, String fragementFilename) throws Exception {
        String key = vertexFilename + ":" + fragementFilename;

        if (shaderProgramMap.containsKey(key)) {
            ShaderProgramHandle handle = shaderProgramMap.get(key);
            while (handle.getShaderProgram() == null) {
                sleep(1);
            }
            //return shaderProgramMap.get(key);
            shaderProgramHandle.set(handle);
        }

        LOGGER.log(Level.FINE, "Loading ShaderProgram: {0}, {1}", new Object[]{vertexFilename, fragementFilename});

        //Shader vertexShader = loadVertexShader(vertexFilename);
        //Shader fragmentShader = loadFragmentShader(fragementFilename);

        ShaderHandle vertexShaderHandle = new ShaderHandle();
        loadVertexShader(vertexShaderHandle, vertexFilename);

        ShaderHandle fragmentShaderHandle = new ShaderHandle();
        loadFragmentShader(fragmentShaderHandle, fragementFilename);

        //shaderProgramMap.put(key, new ShaderProgram(gl, vertexShader, fragmentShader));
        shaderProgramMap.put(key, shaderProgramHandle);
        graphicsEngine.createShaderProgram(shaderProgramHandle, vertexShaderHandle, fragmentShaderHandle);

        //return shaderProgramMap.get(key);
    }


    public void disposeAll() {
        LOGGER.log(Level.FINE, "Disposing resources.");

        for (ShaderProgramHandle shaderProgram : shaderProgramMap.values()) {
            //shaderProgram.dispose(gl);
            graphicsEngine.destroyShaderProgram(shaderProgram);
        }
        shaderProgramMap.clear();

        for (ShaderHandle shader : fragmentShaderMap.values()) {
            //shader.dispose(gl);
            graphicsEngine.destroyShader(shader);
        }
        fragmentShaderMap.clear();

        for (ShaderHandle shader : vertexShaderMap.values()) {
            //shader.dispose(gl);
            graphicsEngine.destroyShader(shader);
        }
        vertexShaderMap.clear();

        for (TextureHandle texture : textureMap.values()) {
            //texture.dispose(gl);
            graphicsEngine.destroyTexture(texture);
        }
        textureMap.clear();

        for (MeshHandle mesh : meshMap.values()) {
            //mesh.dispose(gl);
            graphicsEngine.destroyMesh(mesh);
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
