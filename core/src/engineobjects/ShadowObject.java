package engineobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.mate.engine.MateEngine;
import engineobjects.lights.LightObject;
import enums.ShadowType;
import engineobjects.lights.DayCycleLight;
import scene.SceneObject;
import screen.MateCanvas;

public class ShadowObject {
    private final SceneObject sceneObject;
    private VfxFrameBuffer vfxFrameBuffer;

    private float originOffsetX, originOffsetY, offsetY, maskY;

    public ShadowObject(SceneObject sceneObject) {
        this.sceneObject = sceneObject;
    }

    public void createShadow(ShadowType shadowType, Vector2 position, Batch batch) {
        //-----------------
        for (LightObject light : sceneObject.getSceneLayer().getScene().getCastLights())
            if (shadowType.equals(ShadowType.TYPE_1))
                createType_1(position, light, batch);
            else if (shadowType.equals(ShadowType.TYPE_2))
                createType_2(position, light, batch);

        //-----------------
    }

    private void createType_1(Vector2 position, LightObject light, Batch batch) {
        Vector3 distVec = MateEngine.getDistance(light.getPosition(), sceneObject.getCenterPosition());
        //-----------------
        float degree = MateEngine.getDegree(distVec.x, distVec.y);
        float distance = distVec.z;
        //-----------------
        if (distance >= light.getDistance())
            return;
        //-----------------
        Texture texture;
        if (degree < 270 && degree > -90)
            texture = getTexture(position, batch, true);
        else
            texture = getTexture(position, batch, false);

        TextureRegion region = new TextureRegion(texture);
        region.flip(false, true);
        //-----------------
        float centerX = sceneObject.getWidth() / (2 * MateCanvas.sceneCamera.viewportWidth / Gdx.graphics.getWidth());
        float centerY = sceneObject.getHeight() / (2 * MateCanvas.sceneCamera.viewportHeight / Gdx.graphics.getHeight());
        //-----------------

        float originOffsetX = this.originOffsetX * sceneObject.getResScale().x;
        float originOffsetY = this.originOffsetY * sceneObject.getResScale().y;


        float offsetY = this.offsetY * sceneObject.getResScale().y;
        //-----------------
        if (sceneObject.getSprite().isFlipX() && sceneObject.getSpineObject() == null)
            originOffsetX *= -1;
        //-----------------
        originOffsetX *= 1 / (MateCanvas.sceneCamera.viewportWidth / Gdx.graphics.getWidth());
        originOffsetY *= 1 / (MateCanvas.sceneCamera.viewportHeight / Gdx.graphics.getHeight());
        offsetY *= 1 / (MateCanvas.sceneCamera.viewportHeight / Gdx.graphics.getHeight());
        //-----------------
        centerX += originOffsetX;
        centerY += originOffsetY;
        //-----------------
        float scaleY;
        if (light instanceof DayCycleLight) {
            scaleY = 1.8f - MateEngine.calculateLuminance(((DayCycleLight) light).getCurrentColor());
        } else
            scaleY = 1.8f - (float) Math.exp(-distance / light.getDistance());
        float scaleX = 0.7f / scaleY;
        //-----------------
        Vector3 vp = MateCanvas.sceneCamera.project(new Vector3(position.x, position.y, 0));
        //-----------------
        batch.setProjectionMatrix(MateEngine.getNoProjection());
        if (light instanceof DayCycleLight) {
            batch.setColor(new Color(0, 0, 0, MateEngine.calculateLuminance(((DayCycleLight) light).getCurrentColor())));
        } else
            batch.setColor(new Color(0, 0, 0, 0.6f));


        batch.draw(region, vp.x + 0 * (1 / MateCanvas.sceneCamera.zoom), vp.y + offsetY * (1 / MateCanvas.sceneCamera.zoom), centerX * (1 / MateCanvas.sceneCamera.zoom), centerY * (1 / MateCanvas.sceneCamera.zoom), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scaleX, scaleY, degree + 90);
        batch.setColor(Color.WHITE);
        batch.setProjectionMatrix(MateCanvas.sceneCamera.combined);
        //-----------------

    }

    private void createType_2(Vector2 position, LightObject light, Batch batch) {

    }

    private MaskObject maskObject;

    private Texture getTexture(Vector2 position, Batch batch, boolean flip) {

        if (vfxFrameBuffer == null)
            initFrameBuffer();
        //-----------------
        batch.end();
        //-----------------
        Vector2 screenOrigin = MateCanvas.getScreenOrigin();
        if (flip)
            sceneObject.flipX();
        sceneObject.setPosition(screenOrigin.x, screenOrigin.y);
        //-----------------
        vfxFrameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (sceneObject.getSpineObject() != null)
            sceneObject.getSpineObject().render(batch);
        else
            sceneObject.getSprite().draw(batch);
        batch.end();
        //-----------------
        if (maskY != 0) {
            batch.flush();
            if (maskObject == null)
                maskObject = new MaskObject();
            maskObject.createRecMask(screenOrigin.x, screenOrigin.y, sceneObject.getWidth(), maskY * sceneObject.getResScale().y);
        }
        //-----------------
        vfxFrameBuffer.end();
        //-----------------
        sceneObject.setPosition(position.x, position.y);
        if (flip)
            sceneObject.flipX();
        //-----------------
        batch.begin();
        //-----------------
        return vfxFrameBuffer.getTexture();
    }

    private void initFrameBuffer() {
        vfxFrameBuffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
        vfxFrameBuffer.initialize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        vfxFrameBuffer.dispose();
        if (maskObject != null)
            maskObject.dispose();
    }


    public void setOriginOffsetX(float originOffsetX) {
        this.originOffsetX = originOffsetX;
    }

    public void setOriginOffsetY(float originOffsetY) {
        this.originOffsetY = originOffsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public void setMaskY(float maskY) {
        this.maskY = maskY;
    }
}
