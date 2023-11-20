package engineobjects;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mate.engine.MateEngine;
import com.mate.engine.MateSceneLoader;
import engineobjects.SceneLayer;
import engineobjects.SceneObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import screen.ContentCanvas;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Scene {

    private final MateSceneLoader mateSceneLoader;

    private final String sceneName;
    private Array<PointLight> pointLights;

    private Map<String, TextureAtlas> atlasMap;

    public Scene(String sceneName, MateSceneLoader mateSceneLoader) throws ParserConfigurationException, IOException, SAXException {
        this.sceneName = sceneName;
        this.mateSceneLoader = mateSceneLoader;

        init();
    }

    private World world;
    private RayHandler globalHandler;
    private Array<SceneLayer> sceneLayers;

    private int widthTileCount, heightTileCount;
    private int tileWidth, tileHeight;
    private int sceneWidth, sceneHeight;
    private Color ambientColor;

    private float time;

    public void render(Batch batch) {
        time += Gdx.graphics.getDeltaTime();

        if (time > 1440.0f)
            time = 0;

        batch.setProjectionMatrix(ContentCanvas.camera.combined);
        batch.begin();
        for (SceneLayer sceneLayer : sceneLayers)
            sceneLayer.render(batch);
        batch.end();

        globalHandler.setCombinedMatrix(ContentCanvas.camera);
        globalHandler.updateAndRender();

    }

    private void configureScene(NodeList nodeList) {
        Map<String, String> attributeMap = new HashMap<>();
        Map<String, String> propertyMap = new HashMap<>();

        for (int a = 0; a < nodeList.item(0).getAttributes().getLength(); a++)
            attributeMap.put(nodeList.item(0).getAttributes().item(a).getNodeName(), nodeList.item(0).getAttributes().item(a).getNodeValue());

        for (int a = 0; a < nodeList.item(0).getChildNodes().item(1).getChildNodes().getLength(); a++)
            if (nodeList.item(0).getChildNodes().item(1).getChildNodes().item(a).getNodeName().equals("property"))
                propertyMap.put(nodeList.item(0).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("name").getNodeValue(), nodeList.item(0).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("value").getNodeValue());

        widthTileCount = Integer.parseInt(attributeMap.get("width"));
        heightTileCount = Integer.parseInt(attributeMap.get("height"));
        tileWidth = Integer.parseInt(attributeMap.get("tilewidth"));
        tileHeight = Integer.parseInt(attributeMap.get("tileheight"));

        sceneWidth = widthTileCount * tileWidth;
        sceneHeight = heightTileCount * tileHeight;

        ambientColor = new Color(MateEngine.convertColor((Long.parseLong(propertyMap.get("ambientlight").replace("#", ""), 16))));
    }

    private void init() throws ParserConfigurationException, IOException, SAXException {
        sceneLayers = new Array<>();
        atlasMap = new HashMap<>();
        world = new World(new Vector2(0, 0), false);
        globalHandler = new RayHandler(world);
        pointLights = new Array<>();
        createScene();
    }

    private void createScene() throws ParserConfigurationException, IOException, SAXException {
        Document document = mateSceneLoader.readXmlDocument("Scenes/" + sceneName);
        NodeList nodeList = document.getChildNodes();
        configureScene(nodeList);

        globalHandler.setAmbientLight(ambientColor);

        for (int i = 0; i < nodeList.item(0).getChildNodes().getLength(); i++) {
            String nodeName = nodeList.item(0).getChildNodes().item(i).getNodeName();

            Map<String, String> attributeMap = new HashMap<>();
            Map<String, String> propertyMap = new HashMap<>();

            if (nodeName.equals("layer") || nodeName.equals("objectgroup")) {
                for (int a = 0; a < nodeList.item(0).getChildNodes().item(i).getAttributes().getLength(); a++)
                    attributeMap.put(nodeList.item(0).getChildNodes().item(i).getAttributes().item(a).getNodeName(), nodeList.item(0).getChildNodes().item(i).getAttributes().item(a).getNodeValue());

                nodeList.item(0).getChildNodes().item(i);
                if (nodeList.item(0).getChildNodes().item(i).getChildNodes().getLength() != 0)
                    for (int a = 0; a < nodeList.item(0).getChildNodes().item(i).getChildNodes().item(1).getChildNodes().getLength(); a++)
                        if (nodeList.item(0).getChildNodes().item(i).getChildNodes().item(1).getChildNodes().item(a).getNodeName().equals("property"))
                            propertyMap.put(nodeList.item(0).getChildNodes().item(i).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("name").getNodeValue(), nodeList.item(0).getChildNodes().item(i).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("value").getNodeValue());

            }


            if (nodeName.equals("layer")) {
                SceneLayer sceneLayer = new SceneLayer(this, attributeMap, propertyMap, false);
                sceneLayer.setSceneObjects(createLayer(mateSceneLoader.getBasicLayerMatrix(nodeList.item(0).getChildNodes().item(i).getTextContent()), sceneLayer));
                sceneLayers.add(sceneLayer);
            } else if (nodeName.equals("objectgroup")) {
                SceneLayer sceneLayer = new SceneLayer(this, attributeMap, propertyMap, true);
                sceneLayer.setSceneObjects(createObjectGroup(nodeList.item(0).getChildNodes().item(i), sceneLayer));
                sceneLayers.add(sceneLayer);
            }
        }

    }

    private Array<SceneObject> createLayer(long[][] longArray, SceneLayer sceneLayer) throws ParserConfigurationException, IOException, SAXException {
        Array<SceneObject> sceneObjects = new Array<>();
        for (int countOne = 0; countOne < longArray.length; countOne++)
            for (int countTwo = 0; countTwo < longArray[countOne].length; countTwo++) {
                if (longArray[countOne][countTwo] == 0)
                    continue;

                int[] flags = mateSceneLoader.extractBits(longArray[countOne][countTwo]);
                longArray[countOne][countTwo] = flags[3];

                String[] tilesetInfo = mateSceneLoader.getTilesetInfo((int) longArray[countOne][countTwo], "Scenes/" + sceneName);
                initTileSet(tilesetInfo[1]);
                initTextureAtlas(Gdx.files.internal(tilesetInfo[1]).name().split("\\.")[0]);

                String[] textureKey = Gdx.files.internal(mateSceneLoader.getTilesetData().get(tilesetInfo[1]).get((int) longArray[countOne][countTwo] - Integer.parseInt(tilesetInfo[0])).get("image").get("source")).name().split("\\.");
                System.out.println(textureKey[0]);
                Sprite sprite = new Sprite(atlasMap.get(Gdx.files.internal(tilesetInfo[1]).name().split("\\.")[0]).findRegion(textureKey[0]));
                sprite.setPosition(tileWidth * countTwo, sceneHeight - tileHeight * (countOne + 1));

                if (flags[2] == 1 && flags[0] == 0 && flags[1] == 0) {
                    sprite.rotate90(true);
                    sprite.setFlip(false, true);
                } else if (flags[2] == 1 && flags[0] == 1 && flags[1] == 0) {
                    sprite.rotate90(true);
                    sprite.setFlip(false, false);
                } else if (flags[2] == 1 && flags[0] == 0 && flags[1] == 1) {
                    sprite.rotate90(true);
                    sprite.setFlip(true, true);
                } else if (flags[2] == 1 && flags[0] == 1 && flags[1] == 1) {

                } else {
                    sprite.setFlip(flags[0] != 0, flags[1] != 0);
                }
                sceneObjects.add(new SceneObject(sprite, false, sceneLayer));

            }
        return sceneObjects;
    }

    private Array<SceneObject> createObjectGroup(Node node, SceneLayer sceneLayer) throws ParserConfigurationException, IOException, SAXException {
        Array<SceneObject> sceneObjects = new Array<>();
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {

            if (node.getChildNodes().item(i).getNodeName().equals("#text"))
                continue;
            if (node.getChildNodes().item(i).getNodeName().equals("properties"))
                continue;

            Map<String, String> attributeMap = new HashMap<>();
            for (int a = 0; a < node.getChildNodes().item(i).getAttributes().getLength(); a++)
                attributeMap.put(node.getChildNodes().item(i).getAttributes().item(a).getNodeName(), node.getChildNodes().item(i).getAttributes().item(a).getNodeValue());

            Map<String, String> propertyMap = new HashMap<>();
            if (node.getChildNodes().item(i).getChildNodes() != null && node.getChildNodes().item(i).getChildNodes().getLength() != 0)
                for (int a = 0; a < node.getChildNodes().item(i).getChildNodes().item(1).getChildNodes().getLength(); a++)
                    if (node.getChildNodes().item(i).getChildNodes().item(1).getChildNodes().item(a).getNodeName().equals("property"))
                        propertyMap.put(node.getChildNodes().item(i).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("name").getNodeValue(), node.getChildNodes().item(i).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("value").getNodeValue());

            if (attributeMap.containsKey("type"))
                if (attributeMap.get("type").equals("poly")) {
                    sceneLayer.addScenePoly(attributeMap, propertyMap, node.getChildNodes().item(i).getChildNodes().item(3).getAttributes().getNamedItem("points").getNodeValue());
                    continue;
                } else if (attributeMap.get("type").equals("pointlight")) {
                    sceneLayer.addPointLight(attributeMap, propertyMap);
                    continue;
                } else if (attributeMap.get("type").equals("particle")) {
                    sceneLayer.addParticle(attributeMap, propertyMap);
                    continue;
                }

            long gIdRaw = Long.parseLong((attributeMap.get("gid")));
            int[] flags = mateSceneLoader.extractBits(gIdRaw);
            int gId = flags[3];

            String[] tilesetInfo = mateSceneLoader.getTilesetInfo(gId, "Scenes/" + sceneName);
            initTileSet(tilesetInfo[1]);
            initTextureAtlas(Gdx.files.internal(tilesetInfo[1]).name().split("\\.")[0]);

            Map<String, Map<String, String>> tilesetDataMap = mateSceneLoader.getTilesetData().get(tilesetInfo[1]).get(gId - Integer.parseInt(tilesetInfo[0]));
            String[] textureKey = Gdx.files.internal(tilesetDataMap.get("image").get("source")).name().split("\\.");
            Sprite sprite = new Sprite(atlasMap.get(Gdx.files.internal(tilesetInfo[1]).name().split("\\.")[0]).findRegion(textureKey[0]));
            SceneObject sceneObject = new SceneObject(sprite, true, sceneLayer);

            if (attributeMap.containsKey("rotation")) {
                double centerX = Double.parseDouble(attributeMap.get("width")) / 2;
                double centerY = Double.parseDouble(attributeMap.get("height")) / 2;
                double cosRotation = Math.cos(Math.toRadians(-Double.parseDouble(attributeMap.get("rotation"))));
                double sinRotation = Math.sin(Math.toRadians(-Double.parseDouble(attributeMap.get("rotation"))));

                double rotatedCenterX = centerX * cosRotation - centerY * sinRotation;
                double rotatedCenterY = centerX * sinRotation + centerY * cosRotation;

                double cx = Double.parseDouble(attributeMap.get("x")) + rotatedCenterX;
                double cy = Double.parseDouble(attributeMap.get("y")) + rotatedCenterY;

                double x = cx - centerX;
                double y = cy + centerY;

                attributeMap.put("x", String.valueOf(x));
                attributeMap.put("y", String.valueOf(y));
            }

            if (tilesetDataMap.containsKey("properties"))
                propertyMap.putAll(tilesetDataMap.get("properties"));

            propertyMap.put("reswidth", tilesetDataMap.get("image").get("width"));
            propertyMap.put("resheight", tilesetDataMap.get("image").get("height"));


            sceneObject.initObject(attributeMap, propertyMap);
            sceneObject.setFlip(flags[0] != 0, flags[1] != 0);

            if (propertyMap.containsKey("animation"))
                sceneObject.initSpineAnimation(propertyMap.get("animation"), propertyMap.get("animationfirststate"));
            sceneObjects.add(sceneObject);

        }

        return sceneObjects;

    }

    private void initTextureAtlas(String path) {
        if (atlasMap.containsKey(path))
            return;
        TextureAtlas textureAtlas = new TextureAtlas("TextureAtlases/" + path + ".atlas");
        atlasMap.put(path, textureAtlas);
    }

    private void initTileSet(String path) throws ParserConfigurationException, IOException, SAXException {
        if (mateSceneLoader.getTilesetData().containsKey(path))
            return;
        mateSceneLoader.loadTileSetData(path);
    }


    public void dispose() {
        for (SceneLayer sceneLayer : sceneLayers)
            sceneLayer.dispose();
        world.dispose();
        globalHandler.dispose();

        for (String key : atlasMap.keySet())
            atlasMap.get(key).dispose();
    }

    public int getWidthTileCount() {
        return widthTileCount;
    }

    public int getHeightTileCount() {
        return heightTileCount;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getSceneWidth() {
        return sceneWidth;
    }

    public int getSceneHeight() {
        return sceneHeight;
    }

    public Color getAmbientColor() {
        return ambientColor;
    }

    public Array<PointLight> getPointLights() {
        return pointLights;
    }

    public RayHandler getGlobalHandler() {
        return globalHandler;
    }

    public String getSceneName() {
        return sceneName;
    }

    public World getWorld() {
        return world;
    }

    public float getTime() {
        return time;
    }

    public MateSceneLoader getMateSceneLoader() {
        return mateSceneLoader;
    }

}
