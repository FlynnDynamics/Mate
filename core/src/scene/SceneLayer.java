package scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import engineobjects.LightObject;
import engineobjects.ParticleObject;
import engineobjects.PolyObject;

import java.util.Map;

public class SceneLayer extends Group {

    private Scene scene;

    private Array<ParticleObject> particleObjects;

    private boolean objectGroup;

    public SceneLayer(Scene scene, Map<String, String> attributeMap, Map<String, String> propertyMap, boolean objectGroup) {
        this.scene = scene;
        this.objectGroup = objectGroup;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (particleObjects != null)
            for (ParticleObject particleObject : particleObjects)
                particleObject.render(batch, Gdx.graphics.getDeltaTime());

        for (Actor actor : this.getChildren())
            if (actor instanceof SceneObject)
                ((SceneObject) actor).drawShadow(batch, parentAlpha);

        super.draw(batch, parentAlpha);

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
        new PolyObject(attributeMap, propertyMap, vectors, this);
    }


    public void dispose() {
        for (Actor actor : this.getChildren())
            if (actor instanceof SceneObject) {
                SceneObject sceneObject = (SceneObject) actor;
                sceneObject.dispose();
            }

        if (particleObjects != null)
            for (ParticleObject particleObject : particleObjects)
                particleObject.dispose();
    }

    public Scene getScene() {
        return scene;
    }
}
