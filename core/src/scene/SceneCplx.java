package scene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SceneCplx {
    private final Scene scene;
    private final int groupID;
    private final String cplxFileName;

    public SceneCplx(Scene scene, int groupID, String cplxFileName) {
        this.scene = scene;
        this.groupID = groupID;
        this.cplxFileName = cplxFileName;
    }

    private Vector2 position;
    private Vector2 size;

    private Array<SceneObject> sceneObjects;

    public void addSceneObject(SceneObject sceneObject) {
        if (sceneObjects == null)
            sceneObjects = new Array<>();
        sceneObjects.add(sceneObject);
    }

    public void setSceneObjects(Array<SceneObject> sceneObjects) {
        this.sceneObjects = sceneObjects;
    }

    public Scene getScene() {
        return scene;
    }

    public int getGroupID() {
        return groupID;
    }

    public Array<SceneObject> getSceneObjects() {
        return sceneObjects;
    }

    public String getCplxFileName() {
        return cplxFileName;
    }
}
