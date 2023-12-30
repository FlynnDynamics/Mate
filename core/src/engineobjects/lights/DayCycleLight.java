package engineobjects.lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import scene.Scene;

public class DayCycleLight extends LightObject {

    private Vector2 center;
    private float totalTicks;

    public DayCycleLight(Scene scene, Vector2 center, float distance, float totalTicks) {
        super(scene);
        this.center = center;
        this.distance = distance;
        this.totalTicks = totalTicks;
        scene.getCastLights().add(this);
    }

    public void calculateCyclePosition(float currentTick) {
        float adjustedTick = currentTick - (totalTicks / 24) * 6;
        float angle = (float) (2 * Math.PI - (2 * Math.PI * adjustedTick / totalTicks));
        float x = center.x + (distance / 2) * (float) Math.cos(angle);
        float y = center.y + (distance / 2) * (float) Math.sin(angle);
        setPosition(new Vector2(x, y));
    }

    private Color[] cycleColors;
    private Color currentColor;

    public Color getCycleColor(float currentTick) {
        if (cycleColors == null)
            return null;

        if (currentColor == null)
            currentColor = new Color(Color.WHITE);

        float hour = (currentTick / totalTicks) * 24;
        Color moonlightColor = new Color(cycleColors[0]);
        Color morningColor = new Color(cycleColors[1]);
        Color daylightColor = new Color(cycleColors[2]);
        Color eveningColor = new Color(cycleColors[3]);

        if (hour < 4) {
            currentColor.set(moonlightColor);
        } else if (hour < 6) {
            currentColor.set(moonlightColor.lerp(morningColor, (hour - 4) / 2));
        } else if (hour < 14) {
            currentColor.set(morningColor.lerp(daylightColor, (hour - 6) / 8));
        } else if (hour < 20) {
            currentColor.set(daylightColor.lerp(eveningColor, (hour - 14) / 6));
        } else {
            currentColor.set(eveningColor.lerp(moonlightColor, (hour - 20) / 4));
        }

        return currentColor;
    }

    public Color[] getCycleColors() {
        if (cycleColors == null)
            cycleColors = new Color[4];
        return cycleColors;
    }

    public Color getCurrentColor() {
        if (currentColor == null)
            currentColor = new Color();
        return currentColor;
    }
}
