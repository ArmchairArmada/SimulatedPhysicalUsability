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

/**
 * Manages graphical resources like meshes, textures, shaders, etc.
 */
public class ResourceManager {
    private static final Logger LOGGER = Logger.getLogger(ResourceManager.class.getName());

    // TODO: Refactor to store resources instead of handles?
    private final Map<String, Geometry> geometryMap = new HashMap<>();
    private final Map<String, MeshDescription> meshDescriptionMap = new HashMap<>();
    private final Map<String, MeshHandle> meshMap = new HashMap<>();
    private final Map<String, MaterialHandle> materialMap = new HashMap<>();
    private final Map<String, TextureHandle> textureMap = new HashMap<>();

    private final Map<String, ShaderHandle> vertexShaderMap = new HashMap<>();
    private final Map<String, ShaderHandle> fragmentShaderMap = new HashMap<>();
    private final Map<String, ShaderProgramHandle> shaderProgramMap = new HashMap<>();

    private final IImageLoader imageLoader = new ImageLoader();
    private final IGeometryLoader geometryLoader = new OBJLoader();

    private final ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper for loading JSON files

    /**
     * Default constructor for creating the resource manager
     */
    public ResourceManager() {
    }

    /**
     * Loads geometry from a file
     *
     * @param filename Name of file to load.
     * @param keep     If the geometry should be cached for quick retrieval
     * @return Geometry that has been loaded from a file
     * @throws IOException Thrown if the file cannot be loaded
     */
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

    /**
     * Loads a mesh from a file.  It also loads the material used by the mesh.
     *
     * @param gl             OpenGL
     * @param meshHandle     Mesh handle to store mesh into
     * @param materialHandle Material handle to store material into
     * @param filename       Name of mesh JSON file to load
     * @throws Exception Thrown if file cannot be loaded
     */
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

    /**
     * Loads a material from a file.
     *
     * @param gl             OpenGL
     * @param materialHandle Handle to store material into
     * @param filename       Name of material JSON file
     * @throws Exception Thrown if file cannot be loaded
     */
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

    /**
     * Loads a texture from a file.
     *
     * @param gl            OpenGL
     * @param textureHandle Handle to store texture into
     * @param filename      Name of the image file to load
     * @throws Exception Thrown if file cannot be loaded
     */
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

    /**
     * Loads a vertex shader from file.
     *
     * @param gl           OpenGL
     * @param shaderHandle Handle to store the vertex shader into
     * @param filename     Name of the GLSL vertex shader file
     * @throws Exception Thrown if file cannot be loaded
     */
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

    /**
     * Loads a fragment shader from file.
     *
     * @param gl           OpenGL
     * @param shaderHandle handle to store the fragment shader into
     * @param filename     Name of the GLSL fragment shader file
     * @throws Exception Thrown if file cannot be loaded
     */
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

    /**
     * Generates a shader program from vertex and fragment shaders.
     *
     * @param gl                  OpenGL
     * @param shaderProgramHandle Handle to store shader program into
     * @param vertexFilename      File name of vertex shader to use
     * @param fragmentFilename    File name of fragment shader to use
     * @throws Exception Thrown if files cannot be loaded
     */
    public synchronized void loadShaderProgram(GL3 gl, ShaderProgramHandle shaderProgramHandle, String vertexFilename, String fragmentFilename) throws Exception {
        LOGGER.log(Level.FINE, "Loading ShaderProgram: {0}, {1}", new Object[]{vertexFilename, fragmentFilename});

        String key = vertexFilename + ":" + fragmentFilename;

        if (shaderProgramMap.containsKey(key)) {
            ShaderProgramHandle handle = shaderProgramMap.get(key);
            shaderProgramHandle.set(handle);
            return;
        }
        shaderProgramMap.put(key, shaderProgramHandle);

        ShaderHandle vertexShaderHandle = new ShaderHandle();
        loadVertexShader(gl, vertexShaderHandle, vertexFilename);

        ShaderHandle fragmentShaderHandle = new ShaderHandle();
        loadFragmentShader(gl, fragmentShaderHandle, fragmentFilename);

        shaderProgramHandle.setShaderProgram(new ShaderProgram(gl, vertexShaderHandle.getShader(), fragmentShaderHandle.getShader()));
    }

    /**
     * Disposes of all resources stored in the resource manager
     *
     * @param graphicsEngine The graphics engine, which does the actual destruction
     */
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
