package engineobjects;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.mate.engine.MateEngine;

import java.util.Map;
import java.util.Random;

public class LightObject {
    private Scene scene;
    private PointLight pointLight;
    private Map<String, String> propertyMap;
    private float x, y;

    public LightObject(Scene scene, Map<String, String> propertyMap) {
        this.scene = scene;
        this.propertyMap = propertyMap;
    }

    public void createStaticLight(float x, float y, float width, float height) {
        this.x = x + width / 2;
        this.y = scene.getSceneHeight() - (y + height / 2);
        pointLight = new PointLight(scene.getGlobalHandler(), 150, MateEngine.convertColor(Long.parseLong(propertyMap.get("color").replace("#", ""), 16)), width / 2, this.x, this.y);
        pointLight.setStaticLight(true);
        pointLight.setXray(true);
        if (propertyMap.get("cast").equals("true"))
            scene.getPointLights().add(pointLight);
    }

    public void createObjectLight(float x, float y) {
        this.x = x;
        this.y = y;
        pointLight = new PointLight(scene.getGlobalHandler(), 150, MateEngine.convertColor(Long.parseLong(propertyMap.get("color").replace("#", ""), 16)), Float.parseFloat(propertyMap.get("distance")), this.x, this.y);
        if (propertyMap.get("cast").equals("true"))
            scene.getPointLights().add(pointLight);
    }

    private Random random;
    private float time;

    public void update() {
        time += Gdx.graphics.getDeltaTime();
        if (propertyMap.get("shake").equals("true")) {
            if (time > 0.1f) {
                time = 0;
                if (random == null)
                    random = new Random();
                float x = random.nextFloat(this.x, this.x + 10);
                float y = random.nextFloat(this.y, this.y + 10);
                pointLight.setPosition(x, y);
            }
        } else
            pointLight.setPosition(this.x, this.y);
    }

}
