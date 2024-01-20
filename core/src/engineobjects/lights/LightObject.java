package engineobjects.lights;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mate.engine.MateEngine;
import scene.Scene;

import java.util.Map;
import java.util.Random;

public class LightObject {
    protected final Scene scene;
    protected PointLight pointLight;

    protected Color color;
    protected boolean cast, active, cycle;
    protected Vector2 position;
    protected float distance;

    public LightObject(Scene scene) {
        this.scene = scene;
    }

    public LightObject(Scene scene, Map<String, String> propertyMap) {
        this.scene = scene;
        init(propertyMap);
    }

    private void init(Map<String, String> propertyMap) {
        cast = Boolean.parseBoolean(propertyMap.get("cast"));
        cycle = Boolean.parseBoolean(propertyMap.get("cycle"));

        color = MateEngine.convertColor(Long.parseLong(propertyMap.get("color").replace("#", ""), 16));

        if (propertyMap.containsKey("shake"))
            shake = Boolean.parseBoolean(propertyMap.get("shake"));
        if (propertyMap.containsKey("wobble"))
            wobble = Boolean.parseBoolean(propertyMap.get("wobble"));
        if (propertyMap.containsKey("pulse"))
            pulse = Boolean.parseBoolean(propertyMap.get("pulse"));
        if (propertyMap.containsKey("distance"))
            distance = Float.parseFloat(propertyMap.get("distance"));
    }

    public void createStaticLight(float x, float y, float width, float height) {
        position = new Vector2(x + width / 2, scene.getSceneHeight() - (y + height / 2));
        distance = width / 2;
        pointLight = new PointLight(scene.getGlobalHandler(), 150, color, distance, position.x, position.y);
        pointLight.setStaticLight(true);
        pointLight.setXray(true);

        if (cast)
            scene.getCastLights().add(this);
        scene.getStaticLights().add(this);
    }

    public void createObjectLight(float x, float y) {
        position = new Vector2(x, y);
        pointLight = new PointLight(scene.getGlobalHandler(), 150, color, distance, position.x, position.y);

        if (cast)
            scene.getCastLights().add(this);
    }

    protected boolean shake, wobble, pulse;
    protected Random random;
    protected float timeS, timeW, timeP;

    public void update() {
        if (cycle)
            if (MateEngine.calculateLuminance(scene.getDayCycleLight().getCurrentColor()) >= 0.5f)
                setActive(false);
            else
                setActive(true);

        if (!active)
            return;

        if (shake)
            if (timeS > 0.1f) {
                timeS = 0;
                if (random == null)

                    random = new Random();
                float x = random.nextFloat(position.x, position.x + 10);
                float y = random.nextFloat(position.y, position.y + 10);
                pointLight.setPosition(x, y);
            } else
                timeS += Gdx.graphics.getDeltaTime();
        if (wobble)
            if (timeW > 0.1f) {
                timeW = 0;
                if (random == null)
                    random = new Random();
                float d = random.nextFloat(distance - 50, distance + 50);
                pointLight.setDistance(d);
            } else
                timeW += Gdx.graphics.getDeltaTime();
        if (pulse)
            if (timeP > 0.1f) {
                timeP = 0;
                //Pulse Code
            } else
                timeP += Gdx.graphics.getDeltaTime();
    }

    public void setColor(Color color) {
        this.color = color;
        if (pointLight != null) {
            pointLight.setColor(color);
            pointLight.update();
        }
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        if (pointLight != null) {
            pointLight.setPosition(position.x, position.y);
            pointLight.update();
        }
    }

    public void setDistance(float distance) {
        this.distance = distance;
        if (pointLight != null) {
            pointLight.setDistance(distance);
            pointLight.update();
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        if (pointLight != null)
            pointLight.setActive(active);
    }

    public Vector2 getPosition() {
        if (shake)
            return pointLight.getPosition();
        return position;
    }

    public float getDistance() {
        if (wobble)
            return pointLight.getDistance();
        return distance;
    }

    public Color getColor() {
        return color;
    }

    public boolean isCast() {
        return cast;
    }

    public Scene getScene() {
        return scene;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isShake() {
        return shake;
    }

    public boolean isWobble() {
        return wobble;
    }

    public boolean isPulse() {
        return pulse;
    }

    public Random getRandom() {
        return random;
    }

    public float getTimeS() {
        return timeS;
    }

    public float getTimeW() {
        return timeW;
    }

    public float getTimeP() {
        return timeP;
    }

}
