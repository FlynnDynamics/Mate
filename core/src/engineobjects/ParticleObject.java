package engineobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.mate.engine.MateEngine;
import engineobjects.lights.LightObject;
import scene.SceneLayer;

import java.util.Map;


public class ParticleObject {
    private SceneLayer sceneLayer;
    private ParticleEffect particleEffect;

    private boolean active, cycle;

    private String fileName;
    private float scale;
    private Vector2 position;

    public ParticleObject(SceneLayer sceneLayer, Map<String, String> propertyMap, float x, float y) {
        this.sceneLayer = sceneLayer;
        createParticleEffect(x, y, propertyMap);

    }

    private LightObject lightObject;

    private void createParticleEffect(float x, float y, Map<String, String> propertyMap) {
        position = new Vector2(x, sceneLayer.getScene().getSceneHeight() - y);
        fileName = propertyMap.get("particle");
        scale = Float.parseFloat(propertyMap.get("scale"));
        cycle = Boolean.parseBoolean(propertyMap.get("cycle"));

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particle/Effects/" + fileName), Gdx.files.internal("particle/Images"));

        for (ParticleEmitter emitter : particleEffect.getEmitters())
            if (emitter != null)
                emitter.setPosition(position.x, position.y);


        particleEffect.scaleEffect(scale);

        if (propertyMap.get("light").equals("true")) {
            lightObject = new LightObject(sceneLayer.getScene(), propertyMap);
            lightObject.createObjectLight(position.x, position.y);
        }
        particleEffect.start();

        setActive(true);
    }

    public void render(Batch batch, float delta) {
        if (cycle)
            if (MateEngine.calculateLuminance(sceneLayer.getScene().getDayCycleLight().getCurrentColor()) >= 0.5f)
                setActive(false);
            else
                setActive(true);

        if (!active)
            return;

        if (lightObject != null)
            lightObject.update();

        particleEffect.update(delta);
        particleEffect.draw(batch);
        if (particleEffect.isComplete())
            particleEffect.reset();
    }

    public void setActive(boolean active) {
        this.active = active;
        if (lightObject != null)
            lightObject.setActive(active);
    }

    public void dispose() {
        particleEffect.dispose();
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getFileName() {
        return fileName;
    }

    public float getScale() {
        return scale;
    }

    public Vector2 getPosition() {
        return position;
    }

    public LightObject getLightObject() {
        return lightObject;
    }
}
