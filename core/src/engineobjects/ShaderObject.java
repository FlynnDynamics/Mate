package engineobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.Map;

public class ShaderObject {

    private ShaderProgram shaderProgram;
    private Map<String, String> uniformMap;

    public ShaderObject(Map<String, String> uniformMap) {
        this.uniformMap = uniformMap;
        createShader(uniformMap.get("shadername"));
    }

    private void createShader(String shaderName) {
        if (!Gdx.files.internal("shader/" + shaderName + ".vert").exists() || !Gdx.files.internal("shader/" + shaderName + ".frag").exists()) {
            ready = false;
            return;
        } else {
            shaderProgram = new ShaderProgram(Gdx.files.internal("shader/" + shaderName + ".vert"), Gdx.files.internal("shader/" + shaderName + ".frag"));
        }
        if (shaderProgram.isCompiled())
            ready = true;
        else {
            System.out.println(shaderProgram.getLog());
            ready = false;
        }
    }

    private boolean ready;

    public void update(Batch batch, float tick) {
        if (!ready) {
            return;
        }
        batch.setShader(shaderProgram);
        shaderProgram.setUniformf("u_time", tick);

        for (String key : uniformMap.keySet()) {
            if (key.equals("shader") || key.equals("shadername"))
                continue;

            if (uniformMap.get(key).contains(",")) {
                String[] floatStrings = uniformMap.get(key).split(",");
                if (floatStrings.length > 2) {
                    ready = false;
                    return;
                } else
                    shaderProgram.setUniformf(key, Float.parseFloat(floatStrings[0]), Float.parseFloat(floatStrings[1]));
            } else
                shaderProgram.setUniformf(key, Float.parseFloat(uniformMap.get(key)));
        }
    }

    public void dispose() {
        shaderProgram.dispose();
    }
}
