package engineobjects;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mate.engine.MateEngine;
import scene.Scene;

import java.util.Map;
import java.util.Random;

public class LightObject {
    private Scene scene;
    private PointLight pointLight;

    private Color color;
    private boolean cast;
    private float x, y, distance;

    public LightObject(Scene scene, Map<String, String> propertyMap) {
        this.scene = scene;
        init(propertyMap);
    }

    private void init(Map<String, String> propertyMap) {
        cast = Boolean.parseBoolean(propertyMap.get("cast"));
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
        this.x = x + width / 2;
        this.y = scene.getSceneHeight() - (y + height / 2);
        pointLight = new PointLight(scene.getGlobalHandler(), 150, color, width / 2, this.x, this.y);
        pointLight.setStaticLight(true);
        pointLight.setXray(true);

        if (cast)
            scene.getCastLights().add(pointLight);
        scene.getStaticLights().add(this);
    }

    public void createObjectLight(float x, float y) {
        this.x = x;
        this.y = y;

        pointLight = new PointLight(scene.getGlobalHandler(), 150, color, distance, this.x, this.y);

        if (cast)
            scene.getCastLights().add(pointLight);
    }

    private boolean shake, wobble, pulse;
    private Random random;
    private float timeS, timeW, timeP;

    public void update() {
        if (shake)
            if (timeS > 0.1f) {
                timeS = 0;
                if (random == null)
                    random = new Random();
                float x = random.nextFloat(this.x, this.x + 10);
                float y = random.nextFloat(this.y, this.y + 10);
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
        pointLight.setColor(color);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        pointLight.setPosition(x, y);
    }
}
