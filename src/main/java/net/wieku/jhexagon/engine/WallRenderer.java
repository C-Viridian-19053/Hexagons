package net.wieku.jhexagon.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.wieku.jhexagon.Main;
import net.wieku.jhexagon.api.CurrentMap;
import net.wieku.jhexagon.api.Wall;
import net.wieku.jhexagon.utils.ShapeRenderer3D;
import net.wieku.jhexagon.utils.ShapeRenderer3D.ShapeType;

import java.util.ArrayList;

/**
 * @author Sebastian Krajewski on 21.03.15.
 */
public class WallRenderer {

	Color shadow = new Color();

	public void drawWallsShadow(ShapeRenderer3D renderer, ArrayList<Wall> walls){

		shadow.set(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a).lerp(Color.BLACK, 0.4f);

		for(int j = 0; j< CurrentMap.layers; ++j) {
			renderer.identity();
			renderer.translate(0, -j * CurrentMap.depth, 0);
			//renderer.scale(Wall.pulseSpeed, Wall.pulseSpeed, Wall.pulseSpeed);
			renderer.begin(ShapeType.Filled);
			renderer.setColor(shadow);

			for(int i = 0; i < walls.size(); ++i){

				Wall wall = walls.get(i);

				if(!wall.visible) continue;

				renderer.triangle(wall.tmp.x, wall.tmp.y, wall.tmp2.x, wall.tmp2.y, wall.tmp4.x, wall.tmp4.y);
				renderer.triangle(wall.tmp4.x, wall.tmp4.y, wall.tmp3.x, wall.tmp3.y, wall.tmp.x, wall.tmp.y);

			}

			renderer.end();

		}

		renderer.identity();

	}

	public void drawWalls(ShapeRenderer3D renderer, ArrayList<Wall> walls){

		//renderer.scale(Wall.pulseSpeed, Wall.pulseSpeed, Wall.pulseSpeed);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(CurrentMap.walls.r, CurrentMap.walls.g, CurrentMap.walls.b, CurrentMap.walls.a);

		for(int i = 0; i < walls.size(); ++i){

			Wall wall = walls.get(i);

			if(!wall.visible) continue;

			renderer.triangle(wall.tmp.x, wall.tmp.y, wall.tmp2.x, wall.tmp2.y, wall.tmp4.x, wall.tmp4.y);
			renderer.triangle(wall.tmp4.x, wall.tmp4.y, wall.tmp3.x, wall.tmp3.y, wall.tmp.x, wall.tmp.y);

		}

		renderer.end();

		renderer.identity();

	}

}
