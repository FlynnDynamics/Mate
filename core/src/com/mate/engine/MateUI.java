package com.mate.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;
import screen.MateCanvas;

public class MateUI {
    protected MateCanvas mateCanvas;
    protected Stage stage;

    public MateUI(MateCanvas mateCanvas, Stage stage) {
        this.mateCanvas = mateCanvas;
        this.stage = stage;

        create();
    }

    protected Skin skin;


    public void create() {
        Gdx.input.setInputProcessor(stage);

        stage.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                mateCanvas.scrolled(amountX, amountY);
                return super.scrolled(event, x, y, amountX, amountY);
            }
        });

        skin = new Skin(Gdx.files.internal("UI/metal/metal-ui.json"));
        SceneComposerStageBuilder builder = new SceneComposerStageBuilder();
        builder.build(stage, skin, Gdx.files.internal("UI/metal/mate.json"));

        Slider tickSlider = stage.getRoot().findActor("tick_slider");
        tickSlider.setStepSize(5);
        tickSlider.setRange(0, 1440);
        tickSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                mateCanvas.getCurrentScene().setTime(slider.getValue());
            }
        });

        CheckBox checkBox = stage.getRoot().findActor("tick_box");
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checkBox = (CheckBox) actor;
                mateCanvas.getCurrentScene().setTimeTick(checkBox.isChecked());
            }
        });

    }


    public void render() {
        Label scene = stage.getRoot().findActor("scene");
        scene.setText("current scene: " + mateCanvas.getCurrentScene().getSceneName());
        Label fps = stage.getRoot().findActor("fps");
        fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        Label tick = stage.getRoot().findActor("tick");
        tick.setText("TICK: " + MathUtils.round(mateCanvas.getCurrentScene().getTime()));


        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public MateCanvas getMateCanvas() {
        return mateCanvas;
    }

    public void setMateCanvas(MateCanvas mateCanvas) {
        this.mateCanvas = mateCanvas;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }
}
