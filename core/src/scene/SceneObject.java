package scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mate.engine.MateEngine;
import engineobjects.ShadowObject;
import engineobjects.SpineObject;
import enums.ShadowType;

import java.util.Map;

public class SceneObject extends Sprite {
    private SceneLayer sceneLayer;

    private boolean object;
    private int id;

    private boolean spriteShadow;
    private ShadowObject shadowObject;

    private float resWidth, resHeight;
    private float opacity;

    public SceneObject(Sprite sprite, boolean object, SceneLayer sceneLayer) {
        super(sprite);
        this.object = object;
        this.sceneLayer = sceneLayer;
    }

    public void initObject(Map<String, String> attributeMap, Map<String, String> propertyMap) {
        if (!object)
            return;

        this.setSize(Float.parseFloat(attributeMap.get("width")), Float.parseFloat(attributeMap.get("height")));

        resWidth = Float.parseFloat(propertyMap.get("reswidth"));
        resHeight = Float.parseFloat(propertyMap.get("resheight"));

        this.setPosition(Float.parseFloat(attributeMap.get("x")), sceneLayer.getScene().getSceneHeight() - Float.parseFloat(attributeMap.get("y")));
        this.setOriginCenter();

        id = Integer.parseInt(attributeMap.get("id"));

        if (attributeMap.containsKey("rotation"))
            this.setRotation(-Float.parseFloat(attributeMap.get("rotation")));


        if (propertyMap.containsKey("opacity")) {
            opacity = Float.parseFloat(propertyMap.get("opacity"));
            this.setColor(1, 1, 1, opacity);
        } else
            opacity = 1f;


        if (propertyMap.containsKey("spriteshadow") && propertyMap.get("spriteshadow").equals("true")) {
            spriteShadow = true;
            shadowObject = new ShadowObject(this);
            shadowObject.setOriginOffsetX(Float.parseFloat(propertyMap.get("originoffsetx")));
            shadowObject.setOriginOffsetY(Float.parseFloat(propertyMap.get("originoffsety")));
            shadowObject.setOffsetY(Float.parseFloat(propertyMap.get("offsety")));
            shadowObject.setMaskY(Float.parseFloat(propertyMap.get("masky")));
        }
    }

    public void directDraw(Batch batch) {
        super.draw(batch);
    }


    @Override
    public void draw(Batch batch) {

        if (spriteShadow) {
            Vector2 tmp = new Vector2(this.getX(), this.getY());
            shadowObject.createShadow(ShadowType.TYPE_1, tmp, batch);
        }

        if (spineObject != null)
            spineObject.render(batch);
        else
            super.draw(batch);

        if (object && MateEngine.DEBUG) {
            batch.end();
            MateEngine.addDebugDraw(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            batch.begin();
        }
    }

    private SpineObject spineObject;

    public void initAnimation(String path, String firstState) {
        spineObject = new SpineObject(path, firstState, this);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (spineObject != null)
            spineObject.setPosition(x, y);
    }

    public Vector2 getCenterPosition() {
        return new Vector2((this.getX() + this.getWidth() / 2), (this.getY() + this.getHeight() / 2));
    }

    public Vector2 getResScale() {
        return new Vector2(this.getWidth() / resWidth, this.getHeight() / resHeight);
    }

    public void dispose() {
        this.getTexture().dispose();
        if (spineObject != null)
            spineObject.dispose();
        if (shadowObject != null)
            shadowObject.dispose();
    }

    public SceneLayer getSceneLayer() {
        return sceneLayer;
    }

    public float getResWidth() {
        return resWidth;
    }

    public float getResHeight() {
        return resHeight;
    }

    public SpineObject getSpineObject() {
        return spineObject;
    }
}
