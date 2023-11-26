package engineobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import scene.SceneLayer;

import java.util.Map;

public class PolyObject {

    private SceneLayer sceneLayer;

    private Map<String, String> attributeMap;
    private Map<String, String> propertyMap;
    private BodyDef bodyDef;
    private Body body;


    public PolyObject(Map<String, String> attributeMap, Map<String, String> propertyMap, Vector2[] vertices, SceneLayer sceneLayer) {
        this.attributeMap = attributeMap;
        this.propertyMap = propertyMap;
        this.sceneLayer = sceneLayer;

        initPolyBody(vertices);
    }

    private void initPolyBody(Vector2[] vertices) {

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(Float.parseFloat(attributeMap.get("x")), sceneLayer.getScene().getSceneHeight() - Float.parseFloat(attributeMap.get("y")));
        body = sceneLayer.getScene().getWorld().createBody(bodyDef);
        ChainShape shape = new ChainShape();
        shape.createChain(vertices);
        body.createFixture(shape, 1f);
        shape.dispose();

    }
}
