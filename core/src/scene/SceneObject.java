package scene;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.*;
import com.mate.engine.MateEngine;
import engineobjects.MaskObject;
import screen.MateCanvas;

import java.util.Map;

public class SceneObject extends Sprite {
    private SceneLayer sceneLayer;

    private boolean object;
    private int id;

    private boolean spriteShadow;
    private float originOffsetX, originOffsetY, offsetY, maskY;

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
            originOffsetX = Float.parseFloat(propertyMap.get("originoffsetx"));
            originOffsetY = Float.parseFloat(propertyMap.get("originoffsety"));
            offsetY = Float.parseFloat(propertyMap.get("offsety"));
            maskY = Float.parseFloat(propertyMap.get("masky"));
        }
    }

    private float getDistance(float x, float y) {
        float deltaY = y - (this.getY() + this.getHeight() / 2);
        float deltaX = x - (this.getX() + this.getWidth() / 2);

        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    }

    private float getDegree(float x, float y) {
        float deltaY = y - (this.getY() + this.getHeight() / 2);
        float deltaX = x - (this.getX() + this.getWidth() / 2);

        float atan2 = (float) Math.atan2(deltaY, deltaX);

        return (float) Math.toDegrees(atan2);

    }

    private FrameBuffer shadowBuffer;

    private void initShadowBuffer() {
        shadowBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    private void updateShadowRegion(Batch batch, Texture texture, float lightDistance, float x, float y, float originOffsetX, float originOffsetY, float offsetY) {
        float distance = getDistance(x, y);
        if (distance > lightDistance)
            return;

        float degree = getDegree(x, y);

        TextureRegion region = new TextureRegion(texture);
        region.flip(false, true);

        float centerX = this.getHeight() / (2 * MateCanvas.sceneCamera.viewportWidth / Gdx.graphics.getWidth());
        float centerY = this.getHeight() / (2 * MateCanvas.sceneCamera.viewportHeight / Gdx.graphics.getHeight());

        if (this.isFlipX())
            originOffsetX *= -1;

        originOffsetX *= 1 / (MateCanvas.sceneCamera.viewportWidth / Gdx.graphics.getWidth());
        originOffsetY *= 1 / (MateCanvas.sceneCamera.viewportHeight / Gdx.graphics.getHeight());
        offsetY *= 1 / (MateCanvas.sceneCamera.viewportHeight / Gdx.graphics.getHeight());

        centerX += originOffsetX;
        centerY += originOffsetY;

        float scaleY = 2f - (float) Math.exp(-distance / lightDistance);
        float scaleX = 0.7f / scaleY;

        Vector3 vp = MateCanvas.sceneCamera.project(new Vector3(tempVW.x, tempVW.y, 0));

        batch.setColor(new Color(0, 0, 0, 1));
        batch.draw(region, vp.x + 0 * (1 / MateCanvas.sceneCamera.zoom), vp.y + offsetY * (1 / MateCanvas.sceneCamera.zoom), centerX * (1 / MateCanvas.sceneCamera.zoom), centerY * (1 / MateCanvas.sceneCamera.zoom), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scaleX, scaleY, degree + 90);
        batch.setColor(Color.WHITE);
    }

    private MaskObject maskObject;
    private Vector2 tempVW;
    private SkeletonRenderer skeletonRenderer;

    @Override
    public void draw(Batch batch) {
        if (skeleton != null) {
            animationState.update(Gdx.graphics.getDeltaTime());
            animationState.apply(skeleton);
            skeleton.updateWorldTransform();
        }

        if (spriteShadow) {
            if (shadowBuffer == null)
                initShadowBuffer();
            //-----------------
            batch.end();
            //-----------------
            tempVW = new Vector2(this.getX(), this.getY());
            Vector2 screenOrigin = MateCanvas.getScreenOrigin();
            setPosition(screenOrigin.x, screenOrigin.y);
            //-----------------
            shadowBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
            batch.begin();
            if (skeleton != null)
                skeletonRenderer.draw(batch, skeleton);
            else
                super.draw(batch);
            batch.end();
            if (maskY != 0) {
                batch.flush();
                if (maskObject == null)
                    maskObject = new MaskObject();
                maskObject.createRecMask(screenOrigin.x, screenOrigin.y, this.getWidth(), maskY);
            }
            shadowBuffer.end();
            //-----------------
            setPosition(tempVW.x, tempVW.y);
            //-----------------
            batch.setProjectionMatrix(MateEngine.getNoProjection());
            batch.begin();
            Texture texture = shadowBuffer.getColorBufferTexture();
            for (PointLight light : sceneLayer.getScene().getCastLights())
                updateShadowRegion(batch, texture, light.getDistance(), light.getX(), light.getY(), originOffsetX, originOffsetY, offsetY);
            batch.end();
            batch.setProjectionMatrix(MateCanvas.sceneCamera.combined);
            //-----------------
            batch.begin();
        }

        if (skeleton != null)
            skeletonRenderer.draw(batch, skeleton);
        else
            super.draw(batch);

        if (object && MateEngine.DEBUG) {
            batch.end();
            MateEngine.addDebugDraw(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            batch.begin();
        }
    }

    private AnimationState animationState;
    private TextureAtlas spineAtlas;
    private Skeleton skeleton;

    public void initSpineAnimation(String path, String firstState) {
        spineAtlas = new TextureAtlas(Gdx.files.internal("Skeletons/" + path + "/" + path + ".atlas"));
        SkeletonJson json = new SkeletonJson(spineAtlas);
        skeleton = new Skeleton(json.readSkeletonData(Gdx.files.internal("Skeletons/" + path + "/skeleton.json")));

        skeleton.setScale(this.getWidth() / resWidth, this.getHeight() / resHeight);

        AnimationStateData stateData = new AnimationStateData(skeleton.getData());
        setPosition(this.getX(), this.getY());
        animationState = new AnimationState(stateData);
        animationState.setAnimation(0, firstState, true);

        skeletonRenderer = new SkeletonRenderer();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (skeleton != null) {
            skeleton.setPosition(x - skeleton.getData().getX() * (this.getWidth() / resWidth), y - skeleton.getData().getY() * (this.getHeight() / resHeight));
            skeleton.updateWorldTransform();
        }
    }

    public void dispose() {
        this.getTexture().dispose();
        if (shadowBuffer != null)
            shadowBuffer.dispose();
        if (spineAtlas != null)
            spineAtlas.dispose();
        if (maskObject != null)
            maskObject.dispose();
    }

    public SceneLayer getSceneLayer() {
        return sceneLayer;
    }

}
