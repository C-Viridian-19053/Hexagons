package xyz.hexagons.client.api;

import com.badlogic.gdx.graphics.Color;
import xyz.hexagons.client.map.timeline.Timeline;
import xyz.hexagons.client.map.timeline.TimelineRunnable;

import java.util.ArrayList;

public class GameProperties extends Properties {
    public float rotationSpeed = 0.5f;
    public float rotationSpeedMax = 1.5f;
    public float rotationIncrement = 0.083f;
    public float fastRotate = 70f;
    public transient boolean isFastRotation = false;

    public float difficulty = 1f;
    public float levelIncrement = 15f;
    public float delayMult = 1f;
    public float delayMultInc = 0.01f;
    public float speed = 1f;
    public float speedInc = 0.125f;
    public float currentTime = 0f;

    /**
     * sides
     */
    public int sides = 6;
    public int minSides = 5;
    public int maxSides = 7;
    public boolean mustChangeSides = false;

    /**
     * pulse
     */
    public float beatPulseMin = 1.0f;
    public float beatPulseMax = 1.2f;
    public float beatPulseDelay = 0.5f;
    public float beatPulse = 1.0f;

    /**
     * wallpulse
     */
    public float pulseMin = 70;
    public float pulseMax = 90;
    public float pulseSpeed = 1.0f;
    public float pulseSpeedR = 0.6f;
    public float pulseDelayHalfMax = 0;
    public float pulseDelayMax = 0;
    public float pulse = 75;
    public int pulseDir = 1;

    /**
     * colors
     */
    public ArrayList<HColor> backgroundColors = new ArrayList<>();
    public float colorPulseMin = 0f;
    public float colorPulseMax = 3f;
    public float colorPulseInc = 1f;
    public int colorOffset = 0;
    public float colorSwitch = 1f;
    public HColor walls = new HColor(1, 1, 1, 1);
    public DynamicColor shadow = new DynamicColor() {
        @Override
        public void update() {
            this.set(CurrentMap.gameProperties.walls.r, CurrentMap.gameProperties.walls.g, CurrentMap.gameProperties.walls.b, CurrentMap.gameProperties.walls.a).lerp(Color.BLACK, 0.4f);
        }
    };
    /**
     * gfx settings
     */
    public int layers = 6;
    public float depth = 1.6f;
    public float skew = 0f;
    public float minSkew = 0f;
    public float maxSkew = 1f;
    public float skewTime = 5f;
    public float wallSkewLeft = 0f;
    public float wallSkewRight = 0f;
    public float alphaMultiplier = 1f;
    public float alphaFalloff = 0f;

    public Timeline<Wall> wallTimeline = new Timeline<>();
    public Timeline<TimelineRunnable> eventTimeline = new Timeline<>();
    public boolean gameCompleted = false;
    public boolean useRadians = false;


    @Override
    void registerProperties() {
        registerFloat("rotation.speed", f -> rotationSpeed = f);
        registerFloat("rotation.maxSpeed", f -> rotationSpeedMax = f);
        registerFloat("rotation.increment", f -> rotationIncrement = f);
        registerFloat("rotation.fastRotate", f -> fastRotate = f);

        registerFloat("difficulty.difficulty", f -> difficulty = f);
        registerFloat("difficulty.delayMultiplier", f -> delayMult = f);
        registerFloat("difficulty.delayMultiplierIncrement", f -> delayMultInc = f);
        registerFloat("difficulty.levelIncrement", f -> levelIncrement = f);
        registerFloat("difficulty.speed", f -> speed = f);
        registerFloat("difficulty.speedIncrement", f -> speedInc = f);

        registerInteger("sides.start", i -> sides = i);
        registerInteger("sides.min", i -> minSides = i);
        registerInteger("sides.max", i -> maxSides = i);

        registerFloat("beatPulse.min", f -> beatPulseMin = f);
        registerFloat("beatPulse.max", f -> beatPulseMax = f);
        registerFloat("beatPulse.delay", f -> beatPulseDelay = f);

        registerFloat("pulse.min", f -> pulseMin = f);
        registerFloat("pulse.max", f -> pulseMin = f);
        registerFloat("pulse.speed", f -> pulseMin = f);
        registerFloat("pulse.speedReverse", f -> pulseMin = f);
        registerFloat("pulse.delayHalfMax", f -> pulseDelayHalfMax = f);
        registerFloat("pulse.delayMax", f -> pulseDelayMax = f);
        registerFloat("pulse.min", f -> pulseMin = f);

        registerHColorArray("color.background", backgroundColors);
        registerFloat("color.pulseMin", f -> colorPulseMin = f);
        registerFloat("color.pulseMax", f -> colorPulseMin = f);
        registerFloat("color.pulseIncrement", f -> colorPulseMin = f);
        registerFloat("color.offset", f -> colorOffset = f);
        registerFloat("color.switch", f -> colorSwitch = f);
        registerHColor("color.walls", walls);

        registerInteger("view.layers", i -> layers = i);
        registerFloat("view.depth", i -> depth = i);
        registerFloat("view.skew", i -> skew = i);
        registerFloat("view.minSkew", i -> minSkew = i);
        registerFloat("view.maxSkew", i -> maxSkew = i);
        registerFloat("view.skewTime", i -> skewTime = i);
        registerFloat("view.wallSkewLeft", i -> wallSkewLeft = i);
        registerFloat("view.wallSkewRight", i -> wallSkewRight = i);
        registerFloat("view.alphaMultiplier", i -> alphaMultiplier = i);
        registerFloat("view.alphaFalloff", i -> alphaFalloff = i);
    }
}
