package engineobjects;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mate.engine.MateEngine;
import enums.ShadowType;
import scene.SceneObject;
import screen.MateCanvas;

public class ShadowObject {
    private SceneObject sceneObject;
    private FrameBuffer frameBuffer;

    private float originOffsetX, originOffsetY, offsetY, maskY;

    public ShadowObject(SceneObject sceneObject) {
        this.sceneObject = sceneObject;
    }

    public void createShadow(ShadowType shadowType, Vector2 position, Batch batch) {
        Texture texture = getTexture(position, batch);
        //-----------------
        batch.setProjectionMatrix(MateEngine.getNoProjection());
        batch.begin();
        for (PointLight light : sceneObject.getSceneLayer().getScene().getCastLights())
            if (shadowType.equals(ShadowType.TYPE_1))
                createType_1(texture, position, light, batch);
            else if (shadowType.equals(ShadowType.TYPE_2))
                createType_2(texture, position, light, batch);
        batch.end();
        batch.setProjectionMatrix(MateCanvas.sceneCamera.combined);
        //-----------------
        batch.begin();
    }

    private void createType_1(Texture texture, Vector2 position, PointLight light, Batch batch) {
        Vector3 distVec = MateEngine.getDistance(new Vector2(light.getX(), light.getY()), sceneObject.getCenterPosition());
        //-----------------
        float degree = MateEngine.getDegree(distVec.x, distVec.y);
        float distance = distVec.z;
        //-----------------
        if (distance >= light.getDistance())
            return;
        //-----------------
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
        if (sceneObject.isFlipX() && sceneObject.getSpineObject() == null)
            originOffsetX *= -1;
        //-----------------
        originOffsetX *= 1 / (MateCanvas.sceneCamera.viewportWidth / Gdx.graphics.getWidth());
        originOffsetY *= 1 / (MateCanvas.sceneCamera.viewportHeight / Gdx.graphics.getHeight());
        offsetY *= 1 / (MateCanvas.sceneCamera.viewportHeight / Gdx.graphics.getHeight());
        //-----------------
        centerX += originOffsetX;
        centerY += originOffsetY;
        //-----------------
        float scaleY = 2f - (float) Math.exp(-distance / light.getDistance());
        float scaleX = 0.7f / scaleY;
        //-----------------
        Vector3 vp = MateCanvas.sceneCamera.project(new Vector3(position.x, position.y, 0));
        //-----------------
        batch.setColor(new Color(0, 0, 0, 1));
        batch.draw(region, vp.x + 0 * (1 / MateCanvas.sceneCamera.zoom), vp.y + offsetY * (1 / MateCanvas.sceneCamera.zoom), centerX * (1 / MateCanvas.sceneCamera.zoom), centerY * (1 / MateCanvas.sceneCamera.zoom), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scaleX, scaleY, degree + 90);
        batch.setColor(Color.WHITE);
        //-----------------

    }

    private void createType_2(Texture texture, Vector2 position, PointLight light, Batch batch) {

    }

    private MaskObject maskObject;

    private Texture getTexture(Vector2 position, Batch batch) {
        if (frameBuffer == null)
            initFrameBuffer();
        //-----------------
        batch.end();
        //-----------------
        Vector2 screenOrigin = MateCanvas.getScreenOrigin();
        sceneObject.setPosition(screenOrigin.x, screenOrigin.y);
        //-----------------
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (sceneObject.getSpineObject() != null)
            sceneObject.getSpineObject().render(batch);
        else
            sceneObject.directDraw(batch);
        batch.end();
        //-----------------
        if (maskY != 0) {
            batch.flush();
            if (maskObject == null)
                maskObject = new MaskObject();
            maskObject.createRecMask(screenOrigin.x, screenOrigin.y, sceneObject.getWidth(), maskY * sceneObject.getResScale().y);
        }
        //-----------------
        frameBuffer.end();
        //-----------------
        sceneObject.setPosition(position.x, position.y);
        //-----------------
        return frameBuffer.getColorBufferTexture();
    }

    private void initFrameBuffer() {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public void dispose() {
        frameBuffer.dispose();
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
