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
    private Vector2 rawSize;

    private Array<SceneObject> sceneObjects;

    public void addSceneObject(SceneObject sceneObject) {
        if (sceneObjects == null)
            sceneObjects = new Array<>();
        sceneObjects.add(sceneObject);
    }

    public Vector2 getScale() {
        return new Vector2(size.x / rawSize.x, size.y / rawSize.y);
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setRawSize(Vector2 rawSize) {
        this.rawSize = rawSize;
    }

    public void setSize(Vector2 size) {
        this.size = size;
    }

    public void setSceneObjects(Array<SceneObject> sceneObjects) {
        this.sceneObjects = sceneObjects;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getSize() {
        return size;
    }

    public Vector2 getRawSize() {
        return rawSize;
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
