package engineobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Map;

public class SceneLayer {

    private Scene scene;

    private Array<SceneObject> sceneObjects;
    private Array<ParticleObject> particleObjects;

    private Map<String, String> attributeMap;
    private Map<String, String> propertyMap;
    private boolean objectGroup;

    public SceneLayer(Scene scene, Map<String, String> attributeMap, Map<String, String> propertyMap, boolean objectGroup) {
        this.scene = scene;
        this.attributeMap = attributeMap;
        this.propertyMap = propertyMap;
        this.objectGroup = objectGroup;
    }

    public void render(Batch batch) {
            for (SceneObject sceneObject : sceneObjects)
                sceneObject.draw(batch);

        if (particleObjects != null)
            for (ParticleObject particleObject : particleObjects)
                particleObject.render(batch, Gdx.graphics.getDeltaTime());

    }

    public void addParticle(Map<String, String> attributeMap, Map<String, String> propertyMap) {
        if (particleObjects == null)
            particleObjects = new Array<>();
        ParticleObject particleObject = new ParticleObject(this, propertyMap);
        particleObject.createParticleEffect(Float.parseFloat(attributeMap.get("x")), Float.parseFloat(attributeMap.get("y")));
        particleObjects.add(particleObject);
    }

    public void addPointLight(Map<String, String> attributeMap, Map<String, String> propertyMap) {
        LightObject lightObject = new LightObject(scene, propertyMap);
        lightObject.createStaticLight(Float.parseFloat(attributeMap.get("x")), Float.parseFloat(attributeMap.get("y")), Float.parseFloat(attributeMap.get("width")), Float.parseFloat(attributeMap.get("height")));
    }

    public void addScenePoly(Map<String, String> attributeMap, Map<String, String> propertyMap, String points) {
        String[] vectorStrings = points.split(" ");
        Vector2[] vectors = new Vector2[vectorStrings.length];
        for (int i = 0; i < vectorStrings.length; i++) {
            String[] coordinates = vectorStrings[i].split(",");
            vectors[i] = new Vector2(Float.parseFloat(coordinates[0]), -Float.parseFloat(coordinates[1]));
        }
        new ScenePoly(attributeMap, propertyMap, vectors, this);
    }


    public void addSceneObject(SceneObject sceneObject) {
        if (sceneObjects == null)
            sceneObjects = new Array<>();
        sceneObjects.add(sceneObject);
    }

    public void dispose() {
        for (SceneObject sceneObject : sceneObjects)
            sceneObject.dispose();
        if (particleObjects != null)
            for (ParticleObject particleObject : particleObjects)
                particleObject.dispose();
    }

    public void setSceneObjects(Array<SceneObject> sceneObjects) {
        this.sceneObjects = sceneObjects;
    }

    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }

    public Scene getScene() {
        return scene;
    }
}
