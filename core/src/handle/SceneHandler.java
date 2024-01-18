package handle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import scene.Scene;

public class SceneHandler {

    private Stage sceneStage;
    private SceneEventHandler sceneEventHandler;

    private boolean tick;
    private float gameTick;

    private Scene currentScene;

    public void update() {
        if (tick)
            gameTick += Gdx.graphics.getDeltaTime();
        if (gameTick > 1440.0f)
            gameTick = 0;

        currentScene.render();
    }
}