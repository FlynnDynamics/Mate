package com.mate.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
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

    private Table table;
    private Label labelFPS, labelTICK, labelPOS, labelZOOM;
    private Slider slider;

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

        table = new Table();
        table.setFillParent(true);
        table.align(Align.bottom);
        stage.addActor(table);

        labelFPS = new Label("", skin, "font", "white");
        labelFPS.setFontScale(1.5f);
        addElement(labelFPS);

        labelPOS = new Label("", skin, "font", "white");
        labelPOS.setFontScale(1.5f);
        addElement(labelPOS);

        labelZOOM = new Label("", skin, "font", "white");
        labelZOOM.setFontScale(1.5f);
        addElement(labelZOOM);


        slider = new Slider(0, 1440, 5, false, skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                mateCanvas.getCurrentScene().setTime(slider.getValue());
            }
        });

        labelTICK = new Label("", skin, "font", "white");
        labelTICK.setFontScale(1.5f);
        SplitPane splitPane = new SplitPane(slider, labelTICK, false, skin);
        table.add(splitPane).expandX().left().pad(5);

    }

    public void addElement(Widget element) {
        table.add(element).expandX().left().pad(5);
        table.row();
    }


    public void render() {
        labelTICK.setText(String.format("%-6s %d", " TICK:", Math.round(mateCanvas.getCurrentScene().getTime())));
        labelFPS.setText(String.format("%-6s %d", "FPS:", Gdx.graphics.getFramesPerSecond()));
        Vector3 pos = MateCanvas.sceneCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        labelPOS.setText("POS: " + pos.x + " | " + pos.y);
        labelZOOM.setText(String.format("%-5s %f", "ZOOM:", MateCanvas.sceneCamera.zoom));

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
