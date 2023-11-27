package com.mate.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import scene.Scene;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import screen.MateCanvas;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class MateSceneLoader {
    private Map<String, Map<Integer, Map<String, Map<String, String>>>> tilesetData;

    public Scene getScene(String fileName, Stage sceneStage) throws ParserConfigurationException, IOException, SAXException {
        if (!Gdx.files.internal("Scenes/" + fileName).exists())
            return null;
        return new Scene(fileName, sceneStage, this);
    }

    public MateSceneLoader() throws ParserConfigurationException, IOException, SAXException {
        initAssets();
    }

    private void initAssets() {
        tilesetData = new HashMap<>();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This method loads an XML document from a specified path, processes its nodes to extract tile data, and stores the result in a map.
    public void loadTileSetData(String path) throws ParserConfigurationException, IOException, SAXException {
        Document document = readXmlDocument(path);
        NodeList nodeList = document.getChildNodes();

        Map<Integer, Map<String, Map<String, String>>> tileSetMap = new HashMap<>();
        processNodeList(nodeList, tileSetMap);

        tilesetData.put(path, tileSetMap);
    }

    //Processes each node in the NodeList from the XML document. It specifically looks for 'tile' nodes and processes them further.
    private void processNodeList(NodeList nodeList, Map<Integer, Map<String, Map<String, String>>> tileSetMap) {
        for (int i = 0; i < nodeList.item(0).getChildNodes().getLength(); i++) {
            Node node = nodeList.item(0).getChildNodes().item(i);
            if (node.getNodeName().equals("tile")) {
                processTileNode(node, tileSetMap);
            }
        }
    }

    //Processes an individual 'tile' node. It extracts data from 'properties' and 'image' child nodes and stores it in a map.
    private void processTileNode(Node tileNode, Map<Integer, Map<String, Map<String, String>>> tileSetMap) {
        Map<String, Map<String, String>> tileMap = new HashMap<>();
        for (int a = 0; a < tileNode.getChildNodes().getLength(); a++) {
            Node childNode = tileNode.getChildNodes().item(a);
            if (!childNode.getNodeName().equals("#text")) {
                if (childNode.getNodeName().equals("properties")) {
                    Map<String, String> propertiesMap = processPropertiesNode(childNode);
                    tileMap.put("properties", propertiesMap);
                } else if (childNode.getNodeName().equals("image")) {
                    Map<String, String> imageMap = processImageNode(childNode);
                    tileMap.put("image", imageMap);
                }
            }
        }
        int tileId = Integer.parseInt(tileNode.getAttributes().getNamedItem("id").getNodeValue());
        tileSetMap.put(tileId, tileMap);
    }

    //Processes a 'properties' node, extracting key-value pairs representing properties and storing them in a map
    private Map<String, String> processPropertiesNode(Node propertiesNode) {
        Map<String, String> propertiesMap = new HashMap<>();
        for (int b = 0; b < propertiesNode.getChildNodes().getLength(); b++) {
            Node propertyNode = propertiesNode.getChildNodes().item(b);
            if (!propertyNode.getNodeName().equals("#text")) {
                String name = propertyNode.getAttributes().getNamedItem("name").getNodeValue();
                String value = propertyNode.getAttributes().getNamedItem("value").getNodeValue();
                propertiesMap.put(name, value);
            }
        }
        return propertiesMap;
    }

    //Processes an 'image' node, extracting its attributes and values, and storing them in a map
    private Map<String, String> processImageNode(Node imageNode) {
        Map<String, String> imageMap = new HashMap<>();
        for (int b = 0; b < imageNode.getAttributes().getLength(); b++) {
            Node attribute = imageNode.getAttributes().item(b);
            if (attribute.getNodeName().equals("source"))
                imageMap.put(attribute.getNodeName(), attribute.getNodeValue().replace("../", ""));
            else
                imageMap.put(attribute.getNodeName(), attribute.getNodeValue());
        }
        return imageMap;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String[] getTilesetInfo(int gId, String path) throws ParserConfigurationException, IOException, SAXException {
        Document document = readXmlDocument(path);
        NodeList nodeList = document.getElementsByTagName("tileset");

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (gId >= Integer.parseInt(nodeList.item(i).getAttributes().getNamedItem("firstgid").getNodeValue())) {
                if (i < nodeList.getLength() - 1)
                    if (gId >= Integer.parseInt(nodeList.item(i + 1).getAttributes().getNamedItem("firstgid").getNodeValue()))
                        continue;
                return new String[]{
                        nodeList.item(i).getAttributes().getNamedItem("firstgid").getNodeValue(),
                        nodeList.item(i).getAttributes().getNamedItem("source").getNodeValue().replace("../", "")
                };

            }

        }

        return null;

    }

    //---------------------------------------------------------------------------------------------------------------------------------
    public int[] extractBits(long input) {
        int[] result = new int[4];

        // The first element contains the 32nd bit (index 31).
        result[0] = ((int) input >> 31) & 1;

        // The second element contains the 31st bit (index 30).
        result[1] = ((int) input >> 30) & 1;

        // The third element contains the 30th bit (index 29).
        result[2] = ((int) input >> 29) & 1;

        // The fourth element contains the input with the top four bits subtracted.
        result[3] = (int) input & 0x0FFFFFFF;

        return result;
    }


    public long[][] getBasicLayerMatrix(String s) {
        s = s.replaceAll("(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)", "");
        String[] lines = s.split("\n");

        int numRows = lines.length;
        int numCols = lines[0].split(",").length;
        long[][] longArray = new long[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            String[] values = lines[i].split(",");
            for (int j = 0; j < numCols; j++) {
                longArray[i][j] = Long.parseLong(values[j]);
            }
        }
        return longArray;
    }


    public Document readXmlDocument(String path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(Gdx.files.internal("assets/" + path).readString()));
        Document document = builder.parse(inputSource);
        return document;
    }

    public Map<String, Map<Integer, Map<String, Map<String, String>>>> getTilesetData() {
        return tilesetData;
    }
}
