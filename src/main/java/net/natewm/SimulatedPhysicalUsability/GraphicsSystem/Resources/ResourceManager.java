package net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogamp.opengl.GL3;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.GraphicsEngine.*;
import net.natewm.SimulatedPhysicalUsability.GraphicsSystem.Rendering.*;
import org.joml.Vector4f;

import java.io.IOException;
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

    // TODO: Refactor to store resources instead of handles?
    Map<String, Geometry> geometryMap = new HashMap<>();
    Map<String, MeshDescription> meshDescriptionMap = new HashMap<>();
    Map<String, MeshHandle> meshMap = new HashMap<>();
    Map<String, MaterialHandle> materialMap = new HashMap<>();
    Map<String, TextureHandle> textureMap = new HashMap<>();

    Map<String, ShaderHandle> vertexShaderMap = new HashMap<>();
    Map<String, ShaderHandle> fragmentShaderMap = new HashMap<>();
    Map<String, ShaderProgramHandle> shaderProgramMap = new HashMap<>();

    IImageLoader imageLoader = new ImageLoader();
    IGeometryLoader geometryLoader = new OBJLoader();

    ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper for loading JSON files


    public ResourceManager() {
    }


    private Geometry loadGeometry(String filename, boolean keep) throws IOException {
        LOGGER.log(Level.FINE, "Loading Geometry: {0}", filename);

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


    public synchronized void loadMesh(GL3 gl, MeshHandle meshHandle, MaterialHandle materialHandle, String filename) throws Exception {
        LOGGER.log(Level.FINE, "Loading Mesh: {0}", filename);

        Path path = Paths.get(filename);
        String dir = path.getParent() + "/";

        if (meshDescriptionMap.containsKey(filename)) {
            MeshDescription meshDescription = meshDescriptionMap.get(filename);

            MeshHandle handle = meshMap.get(dir + meshDescription.getGeometry());
            meshHandle.set(handle);
            loadMaterial(gl, materialHandle, dir + meshDescription.getMaterial());
            return;
        }

        // Load mesh description from file
        MeshDescription meshDescription = MeshDescription.loadFromJSON(objectMapper, filename);
        meshDescriptionMap.put(filename, meshDescription);

        // Load geometry
        Geometry geometry = new Geometry();

        for (String geometryFile : meshDescription.getGeometry()) {
            Geometry tmpGeometry = loadGeometry(dir + geometryFile, false);
            geometry.appendGeometry(tmpGeometry);
        }

        // Load material
        loadMaterial(gl, materialHandle, dir + meshDescription.getMaterial());

        // Construct mesh
        Mesh mesh = new Mesh(gl, geometry);
        meshMap.put(dir + meshDescription.getGeometry(), meshHandle);
        meshHandle.setMesh(mesh);
    }


    public synchronized void loadMaterial(GL3 gl, MaterialHandle materialHandle, String filename) throws Exception {
        LOGGER.log(Level.FINE, "Loading Material: {0}", filename);

        // TODO: Figure out how to using the same material with different property values
        if (materialMap.containsKey(filename)) {
            MaterialHandle handle = materialMap.get(filename);
            materialHandle.set(handle);
            return;
        }
        materialMap.put(filename, materialHandle);

        Path path = Paths.get(filename);
        String dir = path.getParent() + "/";

        MaterialDescription materialDescription = MaterialDescription.loadFromJSON(objectMapper, filename);
        ShaderProgramHandle shaderProgramHandle = new ShaderProgramHandle();

        loadShaderProgram(gl, shaderProgramHandle,
                dir + materialDescription.getVertexShader(),
                dir + materialDescription.getFragmentShader()
        );

        //Material material = new Material(gl, shaderProgram);
        MaterialHandle materialHandle1 = new MaterialHandle();
        List<TextureHandle> textureHandles = new ArrayList<>();

        for (String string : materialDescription.getProperties().getTextures()) {
            TextureHandle textureHandle = new TextureHandle();
            textureHandles.add(textureHandle);
            loadTexture(gl, textureHandle, dir + string);
        }

        List<MaterialPropertyHandle> materialPropertyHandles = new ArrayList<>();

        // TODO: Set additional material properties
        // TODO: Find a cleaner way of doing this.
        // diffuseColor
        if (materialDescription.getProperties().getDiffuseColor() != null) {
            List<Double> color = materialDescription.getProperties().getDiffuseColor();

            MaterialPropertyHandle materialPropertyHandle = new MaterialPropertyHandle();

            materialPropertyHandle.setMaterialProperty(new MaterialProperty4f(
                    "diffuseColor",
                    new Vector4f(
                            color.get(0).floatValue(),
                            color.get(1).floatValue(),
                            color.get(2).floatValue(),
                            color.get(3).floatValue()
                    )
            ));

            materialPropertyHandles.add(materialPropertyHandle);
        }

        // specularColor
        if (materialDescription.getProperties().getSpecularColor() != null) {
            List<Double> color = materialDescription.getProperties().getSpecularColor();

            MaterialPropertyHandle materialPropertyHandle = new MaterialPropertyHandle();

            materialPropertyHandle.setMaterialProperty(new MaterialProperty4f(
                    "specularColor",
                    new Vector4f(
                            color.get(0).floatValue(),
                            color.get(1).floatValue(),
                            color.get(2).floatValue(),
                            color.get(3).floatValue()
                    )
            ));

            materialPropertyHandles.add(materialPropertyHandle);
        }

        // specularPower
        if (materialDescription.getProperties().getSpecularPower() != null) {
            double power = materialDescription.getProperties().getSpecularPower();

            MaterialPropertyHandle materialPropertyHandle = new MaterialPropertyHandle();

            materialPropertyHandle.setMaterialProperty(new MaterialProperty1f(
                    "specularPower",
                    (float)power
            ));

            materialPropertyHandles.add(materialPropertyHandle);
        }

        // specularIntensity
        if (materialDescription.getProperties().getSpecularIntensity() != null) {
            double intensity = materialDescription.getProperties().getSpecularIntensity();


            MaterialPropertyHandle materialPropertyHandle = new MaterialPropertyHandle();

            materialPropertyHandle.setMaterialProperty(new MaterialProperty1f(
                    "specularIntensity",
                    (float) intensity
            ));

            materialPropertyHandles.add(materialPropertyHandle);
        }

        Material material = new Material(gl, shaderProgramHandle.getShaderProgram());
        for (TextureHandle textureHandle : textureHandles) {
            material.addTexture(gl, textureHandle.getTexture());
        }
        for (MaterialPropertyHandle materialPropertyHandle : materialPropertyHandles) {
            material.addProperty(gl, materialPropertyHandle.getMaterialProperty());
        }
        materialHandle.setMaterial(material);
    }


    public synchronized void loadTexture(GL3 gl, TextureHandle textureHandle, String filename) throws Exception {
        LOGGER.log(Level.FINE, "Loading Texture: {0}", filename);

        if (textureMap.containsKey(filename)) {
            TextureHandle handle = textureMap.get(filename);
            textureHandle.set(handle);
            return;
        }

        textureMap.put(filename, textureHandle);

        textureHandle.setTexture(new Texture(gl, imageLoader.load(filename)));
    }


    public synchronized void loadVertexShader(GL3 gl, ShaderHandle shaderHandle, String filename) throws Exception {
        LOGGER.log(Level.FINE, "Loading Vertex Shader: {0}", filename);

        if (vertexShaderMap.containsKey(filename)) {
            ShaderHandle handle = vertexShaderMap.get(filename);
            shaderHandle.set(handle);
            return;
        }

        vertexShaderMap.put(filename, shaderHandle);

        Path path = Paths.get(filename);
        String file = path.getFileName().toString();
        String dir = path.getParent() + "/";

        String[] lines = ShaderPreprocessor.load(dir, file);

        shaderHandle.setShader(new Shader(gl, GL3.GL_VERTEX_SHADER, lines));
    }


    public synchronized void loadFragmentShader(GL3 gl, ShaderHandle shaderHandle, String filename) throws Exception {
        LOGGER.log(Level.FINE, "Loading Fragment Shader: {0}", filename);

        if (fragmentShaderMap.containsKey(filename)) {
            ShaderHandle handle = fragmentShaderMap.get(filename);
            shaderHandle.set(handle);
            return;
        }
        fragmentShaderMap.put(filename, shaderHandle);

        Path path = Paths.get(filename);
        String file = path.getFileName().toString();
        String dir = path.getParent() + "/";

        String[] lines = ShaderPreprocessor.load(dir, file);

        shaderHandle.setShader(new Shader(gl, GL3.GL_FRAGMENT_SHADER, lines));
    }


    public synchronized void loadShaderProgram(GL3 gl, ShaderProgramHandle shaderProgramHandle, String vertexFilename, String fragementFilename) throws Exception {
        LOGGER.log(Level.FINE, "Loading ShaderProgram: {0}, {1}", new Object[]{vertexFilename, fragementFilename});

        String key = vertexFilename + ":" + fragementFilename;

        if (shaderProgramMap.containsKey(key)) {
            ShaderProgramHandle handle = shaderProgramMap.get(key);
            shaderProgramHandle.set(handle);
            return;
        }
        shaderProgramMap.put(key, shaderProgramHandle);

        ShaderHandle vertexShaderHandle = new ShaderHandle();
        loadVertexShader(gl, vertexShaderHandle, vertexFilename);

        ShaderHandle fragmentShaderHandle = new ShaderHandle();
        loadFragmentShader(gl, fragmentShaderHandle, fragementFilename);

        shaderProgramHandle.setShaderProgram(new ShaderProgram(gl, vertexShaderHandle.getShader(), fragmentShaderHandle.getShader()));
    }


    public synchronized void disposeAll(GraphicsEngine graphicsEngine) {
        LOGGER.log(Level.FINE, "Disposing resources.");

        // TODO: Proper cleanup
        for (ShaderProgramHandle shaderProgram : shaderProgramMap.values()) {
            graphicsEngine.destroyShaderProgram(shaderProgram);
        }
        shaderProgramMap.clear();

        for (ShaderHandle shader : fragmentShaderMap.values()) {
            graphicsEngine.destroyShader(shader);
        }
        fragmentShaderMap.clear();

        for (ShaderHandle shader : vertexShaderMap.values()) {
            graphicsEngine.destroyShader(shader);
        }
        vertexShaderMap.clear();

        for (TextureHandle texture : textureMap.values()) {
            graphicsEngine.destroyTexture(texture);
        }
        textureMap.clear();

        for (MeshHandle mesh : meshMap.values()) {
            graphicsEngine.destroyMesh(mesh);
        }
        meshMap.clear();

        geometryMap.clear();
    }
}
