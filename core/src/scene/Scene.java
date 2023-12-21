package scene;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mate.engine.MateEngine;
import com.mate.engine.MateSceneLoader;
import engineobjects.lights.DayCycleLight;
import engineobjects.lights.LightObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import screen.MateCanvas;

import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Scene {

    private final MateSceneLoader mateSceneLoader;
    private final String sceneName;
    private final Stage sceneStage;

    public Scene(String sceneName, Stage sceneStage, MateSceneLoader mateSceneLoader) throws ParserConfigurationException, IOException, SAXException {
        this.sceneName = sceneName;
        this.mateSceneLoader = mateSceneLoader;
        this.sceneStage = sceneStage;

        init();
    }

    private World world;
    private RayHandler globalHandler;

    private Array<LightObject> castLights;
    private Array<LightObject> staticLights;

    private Map<String, TextureAtlas> atlasMap;

    private int widthTileCount, heightTileCount;
    private int tileWidth, tileHeight;
    private int sceneWidth, sceneHeight;

    private DayCycleLight dayCycleLight;

    private float time;
    private boolean timeTick;

    public void render() {
        if (timeTick)
            time += Gdx.graphics.getDeltaTime();

        if (time > 1440.0f)
            time = 0;
        if (dayCycleLight != null) {
            dayCycleLight.calculateCyclePosition(time);
            globalHandler.setAmbientLight(dayCycleLight.getCycleColor(time));
        }

        sceneStage.getViewport().apply();
        sceneStage.act();
        sceneStage.draw();

        for (LightObject lightObject : castLights)
            lightObject.update();

        globalHandler.setCombinedMatrix(MateCanvas.sceneCamera);
        globalHandler.updateAndRender();

    }

    private Map<String, String> readSceneAttributes(NodeList nodeList) {
        Map<String, String> attributeMap = new HashMap<>();
        for (int a = 0; a < nodeList.item(0).getAttributes().getLength(); a++)
            attributeMap.put(nodeList.item(0).getAttributes().item(a).getNodeName(), nodeList.item(0).getAttributes().item(a).getNodeValue());
        return attributeMap;
    }

    private Map<String, String> readSceneProperties(NodeList nodeList) {
        Map<String, String> propertyMap = new HashMap<>();
        for (int a = 0; a < nodeList.item(0).getChildNodes().item(1).getChildNodes().getLength(); a++)
            if (nodeList.item(0).getChildNodes().item(1).getChildNodes().item(a).getNodeName().equals("property"))
                propertyMap.put(nodeList.item(0).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("name").getNodeValue(), nodeList.item(0).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("value").getNodeValue());
        return propertyMap;
    }

    private Map<String, String> readLayerAttributes(NodeList nodeList, int index) {
        Map<String, String> attributeMap = new HashMap<>();
        for (int a = 0; a < nodeList.item(0).getChildNodes().item(index).getAttributes().getLength(); a++)
            attributeMap.put(nodeList.item(0).getChildNodes().item(index).getAttributes().item(a).getNodeName(), nodeList.item(0).getChildNodes().item(index).getAttributes().item(a).getNodeValue());
        return attributeMap;
    }

    private Map<String, String> readLayerProperties(NodeList nodeList, int index) {
        Map<String, String> propertyMap = new HashMap<>();
        if (nodeList.item(0).getChildNodes().item(index).getChildNodes().getLength() != 0)
            for (int a = 0; a < nodeList.item(0).getChildNodes().item(index).getChildNodes().item(1).getChildNodes().getLength(); a++)
                if (nodeList.item(0).getChildNodes().item(index).getChildNodes().item(1).getChildNodes().item(a).getNodeName().equals("property"))
                    propertyMap.put(nodeList.item(0).getChildNodes().item(index).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("name").getNodeValue(), nodeList.item(0).getChildNodes().item(index).getChildNodes().item(1).getChildNodes().item(a).getAttributes().getNamedItem("value").getNodeValue());
        return propertyMap;
    }


    private void configureScene(NodeList nodeList) {
        Map<String, String> attributeMap = readSceneAttributes(nodeList);
        Map<String, String> propertyMap = readSceneProperties(nodeList);

        widthTileCount = Integer.parseInt(attributeMap.get("width"));
        heightTileCount = Integer.parseInt(attributeMap.get("height"));
        tileWidth = Integer.parseInt(attributeMap.get("tilewidth"));
        tileHeight = Integer.parseInt(attributeMap.get("tileheight"));

        sceneWidth = widthTileCount * tileWidth;
        sceneHeight = heightTileCount * tileHeight;

        if (propertyMap.containsKey("daycyclelight") && propertyMap.get("daycyclelight").equals("true")) {
            dayCycleLight = new DayCycleLight(this, new Vector2(sceneWidth / 2, sceneHeight / 2), 100000f, 1440.0f);
            dayCycleLight.getCycleColors()[0] = new Color(MateEngine.convertColor((Long.parseLong(propertyMap.get("0").replace("#", ""), 16))));
            dayCycleLight.getCycleColors()[1] = new Color(MateEngine.convertColor((Long.parseLong(propertyMap.get("1").replace("#", ""), 16))));
            dayCycleLight.getCycleColors()[2] = new Color(MateEngine.convertColor((Long.parseLong(propertyMap.get("2").replace("#", ""), 16))));
            dayCycleLight.getCycleColors()[3] = new Color(MateEngine.convertColor((Long.parseLong(propertyMap.get("3").replace("#", ""), 16))));
        } else
            globalHandler.setAmbientLight(new Color(MateEngine.convertColor((Long.parseLong(propertyMap.get("ambientlight").replace("#", ""), 16)))));
    }

    private void init() throws ParserConfigurationException, IOException, SAXException {
        atlasMap = new HashMap<>();
        world = new World(new Vector2(0, 0), false);
        globalHandler = new RayHandler(world);

        castLights = new Array<>();
        staticLights = new Array<>();
        timeTick = true;
        createScene();
    }

    private void createScene() throws ParserConfigurationException, IOException, SAXException {
        Document document = mateSceneLoader.readXmlDocument("Scenes/" + sceneName);
        NodeList nodeList = document.getChildNodes();
        configureScene(nodeList);

        for (int i = 0; i < nodeList.item(0).getChildNodes().getLength(); i++) {
            String nodeName = nodeList.item(0).getChildNodes().item(i).getNodeName();

            Map<String, String> attributeMap, propertyMap;

            if (nodeName.equals("layer") || nodeName.equals("objectgroup")) {
                attributeMap = readLayerAttributes(nodeList, i);
                propertyMap = readLayerProperties(nodeList, i);
            } else
                continue;

            if (nodeName.equals("layer")) {
                SceneLayer sceneLayer = new SceneLayer(this, attributeMap, propertyMap, false);
                createLayer(mateSceneLoader.getBasicLayerMatrix(nodeList.item(0).getChildNodes().item(i).getTextContent()), sceneLayer);
                sceneStage.addActor(sceneLayer);
            } else if (nodeName.equals("objectgroup")) {
                SceneLayer sceneLayer = new SceneLayer(this, attributeMap, propertyMap, true);
                createObjectGroup(nodeList.item(0).getChildNodes().item(i), sceneLayer);
                sceneStage.addActor(sceneLayer);
            }
        }

    }

    private void createLayer(long[][] longArray, SceneLayer sceneLayer) throws ParserConfigurationException, IOException, SAXException {
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
                sceneLayer.addActor(new SceneObject(sprite, false, sceneLayer));
            }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void createObjectGroup(Node node, SceneLayer sceneLayer) throws ParserConfigurationException, IOException, SAXException {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {

            if (node.getChildNodes().item(i).getNodeName().equals("#text"))
                continue;
            if (node.getChildNodes().item(i).getNodeName().equals("properties"))
                continue;

            Map<String, String> attributeMap = extractAttributes(node, i);
            Map<String, String> propertyMap = extractProperties(node, i);

            if (!attributeMap.containsKey("type"))
                attributeMap.put("type", "");

            String type = attributeMap.get("type");
            switch (type) {
                case "poly":
                    sceneLayer.addScenePoly(attributeMap, propertyMap, node.getChildNodes().item(i).getChildNodes().item(3).getAttributes().getNamedItem("points").getNodeValue());
                    continue;
                case "pointlight":
                    sceneLayer.addPointLight(attributeMap, propertyMap);
                    continue;
                case "particle":
                    sceneLayer.addParticle(attributeMap, propertyMap);
                    continue;
                case "complex":
                    createComplexEntity(attributeMap, propertyMap, sceneLayer);
                    continue;
                default:
                    createDefaultEntity(attributeMap, propertyMap, sceneLayer);

            }
        }
    }

    private void createDefaultEntity(Map<String, String> attributeMap, Map<String, String> propertyMap, SceneLayer sceneLayer) throws ParserConfigurationException, IOException, SAXException {

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

        if (attributeMap.containsKey("rotation"))
            applyRotation(attributeMap);

        if (tilesetDataMap.containsKey("properties"))
            tilesetDataMap.get("properties").forEach((key, value) -> propertyMap.putIfAbsent(key, value));

        propertyMap.put("reswidth", tilesetDataMap.get("image").get("width"));
        propertyMap.put("resheight", tilesetDataMap.get("image").get("height"));

        sceneObject.initObject(attributeMap, propertyMap);
        sceneObject.getSprite().setFlip(flags[0] != 0, flags[1] != 0);

        if (propertyMap.containsKey("animation")) {
            sceneObject.initAnimation(propertyMap.get("animation"), propertyMap.get("animationfirststate"));
            sceneObject.getSpineObject().setFlip(flags[0] != 0, flags[1] != 0);
        }

        sceneLayer.addActor(sceneObject);

    }

    private void createComplexEntity(Map<String, String> attributeMap, Map<String, String> propertyMap, SceneLayer sceneLayer) {

    }

    private Map<String, String> extractAttributes(Node node, int index) {
        Map<String, String> attributeMap = new HashMap<>();
        NamedNodeMap attributes = node.getChildNodes().item(index).getAttributes();
        for (int a = 0; a < attributes.getLength(); a++) {
            Node attribute = attributes.item(a);
            attributeMap.put(attribute.getNodeName(), attribute.getNodeValue());
        }
        return attributeMap;
    }

    private Map<String, String> extractProperties(Node node, int index) {
        Map<String, String> propertyMap = new HashMap<>();
        Node childNode = node.getChildNodes().item(index);
        if (childNode.getChildNodes() != null && childNode.getChildNodes().getLength() != 0) {
            NodeList properties = childNode.getChildNodes().item(1).getChildNodes();
            for (int a = 0; a < properties.getLength(); a++) {
                Node propertyNode = properties.item(a);
                if (propertyNode.getNodeName().equals("property")) {
                    NamedNodeMap propertyAttributes = propertyNode.getAttributes();
                    propertyMap.put(propertyAttributes.getNamedItem("name").getNodeValue(), propertyAttributes.getNamedItem("value").getNodeValue());
                }
            }
        }
        return propertyMap;
    }

    private void applyRotation(Map<String, String> attributeMap) {
        double centerX = Double.parseDouble(attributeMap.get("width")) / 2;
        double centerY = Double.parseDouble(attributeMap.get("height")) / 2;
        double rotation = -Double.parseDouble(attributeMap.get("rotation"));
        double cosRotation = Math.cos(Math.toRadians(rotation));
        double sinRotation = Math.sin(Math.toRadians(rotation));

        double rotatedCenterX = centerX * cosRotation - centerY * sinRotation;
        double rotatedCenterY = centerX * sinRotation + centerY * cosRotation;

        double cx = Double.parseDouble(attributeMap.get("x")) + rotatedCenterX;
        double cy = Double.parseDouble(attributeMap.get("y")) - rotatedCenterY;

        double x = cx - centerX;
        double y = cy + centerY;

        attributeMap.put("x", String.valueOf(x));
        attributeMap.put("y", String.valueOf(y));
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void dispose() {
        for (Actor actor : sceneStage.getActors())
            if (actor instanceof SceneLayer sceneLayer) {
                sceneLayer.dispose();
            }

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

    public DayCycleLight getDayCycleLight() {
        return dayCycleLight;
    }

    public Array<LightObject> getCastLights() {
        return castLights;
    }

    public Array<LightObject> getStaticLights() {
        return staticLights;
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

    public void setTime(float time) {
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public MateSceneLoader getMateSceneLoader() {
        return mateSceneLoader;
    }

    public void setTimeTick(boolean timeTick) {
        this.timeTick = timeTick;
    }
}
