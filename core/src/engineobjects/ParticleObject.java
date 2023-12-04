package engineobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import engineobjects.lights.LightObject;
import scene.SceneLayer;

import java.util.Map;


public class ParticleObject {
    private SceneLayer sceneLayer;
    private Map<String, String> propertyMap;
    private ParticleEffect particleEffect;
    private float x, y;

    public ParticleObject(SceneLayer sceneLayer, Map<String, String> propertyMap) {
        this.sceneLayer = sceneLayer;
        this.propertyMap = propertyMap;
    }

    private LightObject lightObject;

    public void createParticleEffect(float x, float y) {
        this.x = x;
        this.y = sceneLayer.getScene().getSceneHeight() - y;

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("Particles/Effects/" + propertyMap.get("p")), Gdx.files.internal("Particles/Images"));

        for (ParticleEmitter emitter : particleEffect.getEmitters())
            if (emitter != null)
                emitter.setPosition(this.x, this.y);


        particleEffect.scaleEffect(Float.parseFloat(propertyMap.get("scale")));

        if (propertyMap.get("light").equals("true")) {
            lightObject = new LightObject(sceneLayer.getScene(), propertyMap);
            lightObject.createObjectLight(this.x, this.y);
        }
        particleEffect.start();
    }

    public void render(Batch batch, float delta) {
        if (lightObject != null)
            lightObject.update();

        particleEffect.update(delta);
        particleEffect.draw(batch);
        if (particleEffect.isComplete())
            particleEffect.reset();
    }

    public void dispose() {
        particleEffect.dispose();
    }
}
