package screen;

import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mate.engine.MateEngine;
import com.mate.engine.MateUI;
import scene.Scene;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class MateCanvas implements Screen, InputProcessor {


    private final MateEngine mateEngine;

    private Stage sceneStage;
    private Stage uiStage;

    public static OrthographicCamera sceneCamera;

    public static Vector2 getScreenOrigin() {
        return new Vector2(sceneCamera.position.x - ((sceneCamera.viewportWidth / 2) * sceneCamera.zoom), sceneCamera.position.y - ((sceneCamera.viewportHeight / 2) * sceneCamera.zoom));
    }

    public MateCanvas(MateEngine mateEngine) {
        this.mateEngine = mateEngine;
        RayHandler.useDiffuseLight(true);
    }

    @Override
    public void show() {
        ExtendViewport fitViewportScene = new ExtendViewport(2560f, 1440f);
        ExtendViewport fitViewportUI = new ExtendViewport(2560f, 1440f);
        sceneStage = new Stage(fitViewportScene);
        uiStage = new Stage(fitViewportUI);
        loadScene();
    }

    private Scene currentScene;
    private MateUI mateUI;

    public void loadScene() {
        try {
            sceneCamera = (OrthographicCamera) sceneStage.getCamera();
            mateUI = new MateUI(this, uiStage);
            currentScene = mateEngine.getMateAssetManager().getScene("map.tmx", sceneStage);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    float interpolationFactor;
    private float zoomSpeed;
    private float targetZoom;

    public void updateCamera() {
        zoomSpeed = 10f;
        zoomSpeed *= Gdx.graphics.getDeltaTime();
        targetZoom = sceneCamera.zoom;
        interpolationFactor = 0.5f * sceneCamera.zoom;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT))
            sceneCamera.zoom = 1;

        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE) && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            float deltaX = Gdx.input.getDeltaX();
            float deltaY = -Gdx.input.getDeltaY();

            deltaX += sceneCamera.position.x;
            deltaY += sceneCamera.position.y;

            sceneCamera.position.lerp(new Vector3(deltaX, deltaY, sceneCamera.position.z), interpolationFactor);
        }
        sceneCamera.update();
    }


    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            dispose();
            loadScene();
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        //Gdx.gl.glClearColor(0, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        updateCamera();
        currentScene.render();
        mateUI.render();
    }

    @Override
    public void dispose() {
        currentScene.dispose();
        sceneStage.clear();
        uiStage.clear();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    @Override
    public void resize(int width, int height) {
        sceneStage.getViewport().update(width, height, false);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public static MateCanvas instance;

    public static MateCanvas getInstance() throws ParserConfigurationException, IOException, SAXException {
        return instance;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;

    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            if (amountY > 0)
                targetZoom += zoomSpeed;
            else if (amountY < 0)
                targetZoom -= zoomSpeed;

            sceneCamera.zoom = MathUtils.lerp(sceneCamera.zoom, targetZoom, interpolationFactor);
            sceneCamera.update();
        }
        return false;
    }

    public OrthographicCamera getCamera() {
        return sceneCamera;
    }
}
