package pro.shpin.kirill.nin.model;

import pro.shpin.kirill.nin.model.enemies.Enemy;

public class Wall {

	public float x;
	public float y;
	public float width;
	public float height;

	private Enemy enemy;

	public Wall(float x, float y, float width, float height, Enemy enemy) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.enemy = enemy;
	}

	public void updateState(Game game, float interval) {
		if (enemy != null) enemy.updateState(game, interval);
	}

	public Enemy getEnemy() {
		return enemy;
	}

	public void nullifyEnemy() {
		this.enemy = null;
	}
}
