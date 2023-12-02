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
    private MateSceneLoader mateSceneLoader;
    private MateCanvas mateCanvas;

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    @Override
    public void create() {
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

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
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

    public static float calculateLuminance(Color color) {
        float red = color.r;
        float green = color.g;
        float blue = color.b;

        float a = 0.2126f * red + 0.7152f * green + 0.0722f * blue;

        if (a <= 0.1f)
            return 0.1f;
        else if (a >= 0.6f)
            return 0.6f;
        return a;


    }

    public MateSceneLoader getMateAssetManager() {
        return mateSceneLoader;
    }
}
