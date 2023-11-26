package com.mate.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.xml.sax.SAXException;
import screen.MateCanvas;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class MateEngine extends Game {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera camera;
    private MateSceneLoader mateSceneLoader;
    private MateCanvas mateCanvas;

    public static boolean DEBUG;

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        try {
            mateSceneLoader = new MateSceneLoader();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        setScreen(mateCanvas = new MateCanvas(this));
    }


    private ArrayList<Float[]> debugResolutionInfo;

    private void initDebugResolutionInfo() {
        if (debugResolutionInfo == null)
            debugResolutionInfo = new ArrayList<>();

        // debugResolutionInfo.add(new Float[]{1920f, 1080f});
        debugResolutionInfo.add(new Float[]{2560f, 1440f});
        //  debugResolutionInfo.add(new Float[]{2880f, 1620f});
        //debugResolutionInfo.add(new Float[]{3840f, 2160f});
    }

    @Override
    public void render() {
        super.render();

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.getData().setScale(1);
        font.draw(batch, "PosX " + mateCanvas.getCamera().position.x + " PosY " + mateCanvas.getCamera().position.y, 0, 15);
        font.draw(batch, "Scale: " + mateCanvas.getCamera().zoom, 0, 30);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, 45);
        font.draw(batch, "Tick: " + mateCanvas.getScene().getTime(), 0, 60);
        font.draw(batch, "Delta : " + Gdx.graphics.getDeltaTime(), 0, 75);
        font.draw(batch, "Current Scene: " + mateCanvas.getScene().getSceneName(), 0, Gdx.graphics.getHeight());
        font.draw(batch, "Scene Size : " + mateCanvas.getScene().getWidthTileCount() + " X " + mateCanvas.getScene().getHeightTileCount() + " Tile: " + mateCanvas.getScene().getTileWidth() + "px", 0, Gdx.graphics.getHeight() - 15);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 5);
        shapeRenderer.end();

        shapeRenderer.setProjectionMatrix(mateCanvas.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(0, 0, mateCanvas.getScene().getSceneWidth(), mateCanvas.getScene().getSceneHeight());

        if (DEBUG) {
            debugRenderer();
            if (debugResolutionInfo == null)
                initDebugResolutionInfo();
            for (Float[] floats : debugResolutionInfo) {

                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.rect(mateCanvas.getCamera().position.x - floats[0] / 2, mateCanvas.getCamera().position.y - floats[1] / 2, floats[0], floats[1]);
                shapeRenderer.setColor(Color.WHITE);

                batch.setProjectionMatrix(mateCanvas.getCamera().combined);
                batch.begin();
                font.getData().setScale(3);
                font.draw(batch, floats[0] + " * " + floats[1], mateCanvas.getCamera().position.x - floats[0] / 2, mateCanvas.getCamera().position.y + floats[1] / 2);
                batch.end();
            }
        }
        shapeRenderer.end();
    }

    public static ArrayList<float[]> recs;

    public static void addDebugDraw(float x, float y, float w, float h) {
        if (recs == null)
            recs = new ArrayList<>();
        recs.add(new float[]{x, y, w, h});
    }

    public void debugRenderer() {
        for (float[] floats : recs)
            shapeRenderer.rect(floats[0], floats[1], floats[2], floats[3]);

        recs.clear();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    public static Vector3 getDistance(Vector2 p1, Vector2 p2) {
        float deltaY = p1.y - p2.y;
        float deltaX = p1.x - p2.x;
        return new Vector3(deltaX, deltaY, (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY));
    }

    public static float getDegree(float deltaX, float deltaY) {
        return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    public static Matrix4 getNoProjection() {
        OrthographicCamera noProjectionCamera = new OrthographicCamera();
        noProjectionCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return noProjectionCamera.combined;
    }

    public static Color convertColor(long hex) {
        float a = (hex & 0xFF000000L) >> 24;
        float r = (hex & 0xFF0000L) >> 16;

        float g = (hex & 0xFF00L) >> 8;
        float b = (hex & 0xFFL);
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }


    public MateSceneLoader getMateAssetManager() {
        return mateSceneLoader;
    }
}
