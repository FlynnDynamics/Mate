package scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mate.engine.MateEngine;
import engineobjects.ParticleObject;
import engineobjects.ShadowObject;
import engineobjects.SpineObject;
import enums.ShadowType;
import screen.MateCanvas;

import java.util.Map;

public class SceneObject extends Actor {
    private final SceneLayer sceneLayer;
    private final Sprite sprite;

    private final boolean object;
    private int objectID;

    private SceneCplx sceneCplx;

    private boolean shadow;
    private ShadowObject shadowObject;

    private float resWidth, resHeight;

    public SceneObject(Sprite sprite, boolean object, SceneLayer sceneLayer) {
        this.sprite = sprite;
        this.object = object;
        this.sceneLayer = sceneLayer;
    }

    public void initObject(Map<String, String> attributeMap, Map<String, String> propertyMap, SceneCplx sceneCplx) {
        if (!object)
            return;

        if (sceneCplx != null)
            this.sceneCplx = sceneCplx;

        setSize(Float.parseFloat(attributeMap.get("width")), Float.parseFloat(attributeMap.get("height")));
        setPosition(Float.parseFloat(attributeMap.get("x")), sceneLayer.getScene().getSceneHeight() - Float.parseFloat(attributeMap.get("y")));

        resWidth = Float.parseFloat(propertyMap.get("reswidth"));
        resHeight = Float.parseFloat(propertyMap.get("resheight"));

        sprite.setOriginCenter();

        this.setOrigin(sprite.getOriginX(), sprite.getOriginY());

        objectID = Integer.parseInt(attributeMap.get("id"));

        if (attributeMap.containsKey("rotation"))
            setRotation(-Float.parseFloat(attributeMap.get("rotation")));

        if (propertyMap.containsKey("shadow") && propertyMap.get("shadow").equals("true")) {
            shadow = true;
            shadowObject = new ShadowObject(this, Enum.valueOf(ShadowType.class, propertyMap.get("shadowtype")));
            shadowObject.setOriginOffsetX(Float.parseFloat(propertyMap.get("originoffsetx")));
            shadowObject.setOriginOffsetY(Float.parseFloat(propertyMap.get("originoffsety")));
            shadowObject.setOffsetY(Float.parseFloat(propertyMap.get("offsety")));
            shadowObject.setMaskY(Float.parseFloat(propertyMap.get("masky")));
        }
    }

    private SpineObject spineObject;

    public void initAnimation(String path, String firstState) {
        spineObject = new SpineObject(path, firstState, this);
    }

    private Array<ParticleObject> particleObjects;

    public void drawShadow(Batch batch, float parentAlpha) {
        if (shadow && MateEngine.isInView(MateCanvas.sceneCamera, getCenterPosition())) {
            Vector2 tmp = new Vector2(this.getX(), this.getY());
            shadowObject.createShadow(tmp, batch);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (spineObject != null)
            spineObject.render(batch);
        else
            sprite.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {

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

    public void setFlip(boolean flipX, boolean flipY) {
        sprite.flip(flipX, flipY);
        if (spineObject != null)
            spineObject.setFlip(flipX, flipY);
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

    public SceneCplx getSceneCplx() {
        return sceneCplx;
    }
}
