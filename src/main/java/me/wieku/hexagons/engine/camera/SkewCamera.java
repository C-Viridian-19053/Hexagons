package me.wieku.hexagons.engine.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import me.wieku.hexagons.api.CurrentMap;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class SkewCamera extends PerspectiveCamera {

	private float currentRotation;
	public float rumbleX;
	public float rumbleZ;
	private float rumbleTime = 0;
	private float currentRumbleTime = 1;
	private float rumblePower = 0;
	private float currentRumblePower = 0;


	public SkewCamera(){
		viewportWidth = 4;
		viewportHeight = 3;
		far = 1000000f;
		near = 0.00001f;
		fieldOfView = 45f;
		update();
	}


	float delta0, pulseInc=-1f, skew=0, dr =-1;

	public void update(float delta){

		pulseInc = (pulseInc<0?-1:1) * Math.abs(CurrentMap.data.colorPulseInc);

		delta0+=delta * 60 * 2.5f * pulseInc;
		if(delta0 < -CurrentMap.data.colorPulse/2){
			delta0 = -CurrentMap.data.colorPulse/2;
			pulseInc *= -1f;
		}
		if(delta0 > CurrentMap.data.colorPulse/2){
			delta0 = CurrentMap.data.colorPulse/2;
			pulseInc *= -1f;
		}

		//float percent = (delta0 / (CurrentMap.data.colorPulse*(  (dr< 0 && pulseInc<0) || (dr>0 && pulseInc>0) ?50:60)));

		if(currentRumbleTime <= rumbleTime) {
			currentRumblePower = rumblePower * ((rumbleTime - currentRumbleTime) / rumbleTime);

			rumbleX = (MathUtils.random(1.0f) - 0.5f) * 2 * currentRumblePower;
			rumbleZ = (MathUtils.random(1.0f) - 0.5f) * 2 * currentRumblePower;

			currentRumbleTime += delta;
		} else {
			rumbleX = 0;
			rumbleZ = 0;
		}

		skew += (delta0 / (CurrentMap.data.colorPulse * (((dr< 0 && delta0<0) || (dr>0 && delta0>0)) ?45f:60f)) );

		if (skew >= 1) {
			skew = 1;
			dr = -1;
		}

		if (skew <= 0) {
			skew = 0;
			dr = 1;
		}

		//System.out.println(skew);
		position.set(0, 1200f, 0).rotate(Vector3.X, MathUtils.clamp(50f * CurrentMap.data.skew, 0.00001f, 50f)).rotate(Vector3.Y, currentRotation).add(rumbleX, 0, rumbleZ);

		lookAt(rumbleX, 0, rumbleZ);

		up.set(0, (CurrentMap.data.skew >= 0 ? 1 : -1), 0);

		update();
	}

	public void rotate(float angle){
		currentRotation += angle;
		currentRotation = (currentRotation >= 360f ? currentRotation - 360f : (currentRotation >= 360f? 360f - currentRotation : currentRotation));
	}

	public void rumble(float power, float time) {
		rumblePower = power;
		rumbleTime = time;
		currentRumbleTime = 0;
	}

	public void reset(){
		rumblePower = 0;
		rumbleTime = 1;
		currentRumbleTime = 0;
		currentRotation = 0;
	}
}
