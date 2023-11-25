package screen;

import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mate.engine.MateEngine;
import engineobjects.Scene;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ContentCanvas implements Screen, InputProcessor {


    private MateEngine mateEngine;

    private Scene scene;
    private SpriteBatch batch;
    public static OrthographicCamera camera;

    public ContentCanvas(MateEngine mateEngine) {
        this.mateEngine = mateEngine;
        RayHandler.useDiffuseLight(true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        loadScene();
    }

    public void loadScene() {
        try {
            batch = new SpriteBatch();
            batch.maxSpritesInBatch = 50000;

            camera = new OrthographicCamera();
            camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Viewport viewport = new FitViewport(2560f, 1440f, camera);
            viewport.apply();

            scene = mateEngine.getMateAssetManager().getScene("map.tmx");
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    float interpolationFactor;
    private float zoomSpeed;
    private float targetZoom;

    public void updateCamera() {
        zoomSpeed = 10f;
        zoomSpeed *= Gdx.graphics.getDeltaTime();
        targetZoom = camera.zoom;
        interpolationFactor = 0.5f * camera.zoom;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT))
            camera.zoom = 1;

        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE) && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            float deltaX = Gdx.input.getDeltaX();
            float deltaY = -Gdx.input.getDeltaY();

            deltaX += camera.position.x;
            deltaY += camera.position.y;

            camera.position.lerp(new Vector3(deltaX, deltaY, camera.position.z), interpolationFactor);
        }
        camera.update();
    }


    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            Matrix4 tempM = camera.combined;
            Vector3 temV = camera.position;
            float zoom = camera.zoom;

            dispose();
            loadScene();

            camera.combined.set(tempM);
            camera.position.set(temV);
            camera.zoom = zoom;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (MateEngine.DEBUG)
                MateEngine.DEBUG = false;
            else
                MateEngine.DEBUG = true;
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        //Gdx.gl.glClearColor(0, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        updateCamera();
        scene.render(batch);
    }

    @Override
    public void dispose() {
        scene.dispose();
        batch.dispose();
    }

    public Scene getScene() {
        return scene;
    }

    @Override
    public void resize(int width, int height) {

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

    public static ContentCanvas instance;

    public static ContentCanvas getInstance() throws ParserConfigurationException, IOException, SAXException {
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

            camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, interpolationFactor);
            camera.update();
        }
        return false;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
