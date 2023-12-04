package engineobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import scene.SceneObject;

public class SpineObject {

    private SceneObject sceneObject;

    private AnimationState animationState;
    private TextureAtlas spineAtlas;
    private Skeleton skeleton;

    public SpineObject(String path, String firstState, SceneObject sceneObject) {
        this.sceneObject = sceneObject;
        initSpine(path, firstState);
    }

    private void initSpine(String path, String firstState) {
        spineAtlas = new TextureAtlas(Gdx.files.internal("Skeletons/" + path + "/" + path + ".atlas"));
        SkeletonJson json = new SkeletonJson(spineAtlas);
        skeleton = new Skeleton(json.readSkeletonData(Gdx.files.internal("Skeletons/" + path + "/skeleton.json")));

        skeleton.setScale(sceneObject.getResScale().x, sceneObject.getResScale().y);
        AnimationStateData stateData = new AnimationStateData(skeleton.getData());
        setPosition(sceneObject.getX(), sceneObject.getY());
        animationState = new AnimationState(stateData);
        animationState.setAnimation(0, firstState, true);
    }

    private SkeletonRenderer skeletonRenderer;

    public void render(Batch batch) {
        if (skeletonRenderer == null)
            skeletonRenderer = new SkeletonRenderer();

        animationState.update(Gdx.graphics.getDeltaTime());
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
        skeletonRenderer.draw(batch, skeleton);
    }

    public void setPosition(float x, float y) {
        skeleton.setPosition(x - skeleton.getData().getX() * sceneObject.getResScale().x, y - skeleton.getData().getY() * sceneObject.getResScale().y);
        skeleton.updateWorldTransform();
    }

    public boolean isFlipedX() {
        if (skeleton.getScaleX() < 0)
            return true;
        return false;
    }

    public boolean isFlipedY() {
        if (skeleton.getScaleY() < 0)
            return true;
        return false;
    }

    public void setFlip(boolean x, boolean y) {
        if (x)
            skeleton.setScaleX(skeleton.getScaleX() * -1);
        if (y) {
            skeleton.setScaleY(skeleton.getScaleY() * -1);
        }
    }

    public void dispose() {
        spineAtlas.dispose();
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    public TextureAtlas getSpineAtlas() {
        return spineAtlas;
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }
}
