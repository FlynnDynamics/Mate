package scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mate.engine.MateEngine;
import engineobjects.ShadowObject;
import engineobjects.SpineObject;
import enums.ShadowType;

import java.util.Map;

public class SceneObject extends Actor {
    private SceneLayer sceneLayer;
    private Sprite sprite;

    private boolean object;
    private int id;
//Test
    private boolean spriteShadow;
    private ShadowObject shadowObject;

    private float resWidth, resHeight;

    public SceneObject(Sprite sprite, boolean object, SceneLayer sceneLayer) {
        this.sprite = sprite;
        this.object = object;
        this.sceneLayer = sceneLayer;
    }

    public void initObject(Map<String, String> attributeMap, Map<String, String> propertyMap) {
        if (!object)
            return;

        setSize(Float.parseFloat(attributeMap.get("width")), Float.parseFloat(attributeMap.get("height")));
        setPosition(Float.parseFloat(attributeMap.get("x")), sceneLayer.getScene().getSceneHeight() - Float.parseFloat(attributeMap.get("y")));

        resWidth = Float.parseFloat(propertyMap.get("reswidth"));
        resHeight = Float.parseFloat(propertyMap.get("resheight"));

        sprite.setOriginCenter();

        this.setOrigin(sprite.getOriginX(), sprite.getOriginY());

        id = Integer.parseInt(attributeMap.get("id"));

        if (attributeMap.containsKey("rotation"))
            setRotation(-Float.parseFloat(attributeMap.get("rotation")));

        if (propertyMap.containsKey("spriteshadow") && propertyMap.get("spriteshadow").equals("true")) {
            spriteShadow = true;
            shadowObject = new ShadowObject(this);
            shadowObject.setOriginOffsetX(Float.parseFloat(propertyMap.get("originoffsetx")));
            shadowObject.setOriginOffsetY(Float.parseFloat(propertyMap.get("originoffsety")));
            shadowObject.setOffsetY(Float.parseFloat(propertyMap.get("offsety")));
            shadowObject.setMaskY(Float.parseFloat(propertyMap.get("masky")));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (spriteShadow) {
            Vector2 tmp = new Vector2(this.getX(), this.getY());
            shadowObject.createShadow(ShadowType.TYPE_1, tmp, batch);
        }
        if (spineObject != null)
            spineObject.render(batch);
        else
            sprite.draw(batch, parentAlpha);

        if (object && MateEngine.DEBUG) {
            batch.end();
            MateEngine.addDebugDraw(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            batch.begin();
        }
    }

    @Override
    public void act(float delta) {

    }

    private SpineObject spineObject;

    public void initAnimation(String path, String firstState) {
        spineObject = new SpineObject(path, firstState, this);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        sprite.setSize(width, height);
    }

    @Override
    public void setRotation(float degrees) {
        super.setRotation(degrees);
        sprite.setRotation(degrees);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        sprite.setPosition(x, y);
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
        sprite.getTexture().dispose();
        if (spineObject != null)
            spineObject.dispose();
        if (shadowObject != null)
            shadowObject.dispose();
    }

    public Sprite getSprite() {
        return sprite;
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
