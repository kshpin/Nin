package pro.shpin.kirill.nin.model;

import pro.shpin.kirill.nin.model.enemies.ArcherEnemy;
import pro.shpin.kirill.nin.model.enemies.Enemy;
import pro.shpin.kirill.nin.model.enemies.SwordEnemy;

import java.util.ArrayList;
import java.util.List;

public class SectionPrototype {

	private static final int NUM_OF_WALLS = 0;
	private static final int NUM_OF_WALL_PARAMS = 5;

	static final int NO_ENEMY = 0;
	static final int SWORD_ENEMY = 1;
	static final int ARCHER_ENEMY = 2;

	private float[] config;

	public SectionPrototype(float[] config) {
		this.config = config;
	}

	public Section build(int sectionIndex) {
		List<Wall> walls = new ArrayList<>();

		int numOfWalls = (int) config[NUM_OF_WALLS];
		for (int i = 0; i < numOfWalls; i++) {
			Enemy enemy = null;
			if (config[i*NUM_OF_WALL_PARAMS+5] == SWORD_ENEMY)
				enemy = new SwordEnemy(
						config[i*NUM_OF_WALL_PARAMS+1],
						config[i*NUM_OF_WALL_PARAMS+2] + config[i*NUM_OF_WALL_PARAMS+4] + sectionIndex*Game.PIXELS_PER_SECTION,
						config[i*NUM_OF_WALL_PARAMS+3]
				);
			if (config[i*NUM_OF_WALL_PARAMS+5] == ARCHER_ENEMY)
				enemy = new ArcherEnemy(
						config[i*NUM_OF_WALL_PARAMS+1] + config[i*NUM_OF_WALL_PARAMS+3]/2f,
						config[i*NUM_OF_WALL_PARAMS+2] + config[i*NUM_OF_WALL_PARAMS+4] + sectionIndex*Game.PIXELS_PER_SECTION
				);

			walls.add(
					new Wall(
							config[i*NUM_OF_WALL_PARAMS+1],
							config[i*NUM_OF_WALL_PARAMS+2] + sectionIndex*Game.PIXELS_PER_SECTION,
							config[i*NUM_OF_WALL_PARAMS+3],
							config[i*NUM_OF_WALL_PARAMS+4],
							enemy
					)
			);
		}

		return new Section(walls);
	}
}
