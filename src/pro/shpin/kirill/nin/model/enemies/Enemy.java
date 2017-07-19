package pro.shpin.kirill.nin.model.enemies;

import pro.shpin.kirill.nin.model.Game;
import pro.shpin.kirill.nin.model.Player;

public abstract class Enemy {

	float posX;
	float posY;

	boolean isAlive = true;

	Enemy(float x, float y) {
		this.posX = x;
		this.posY = y + Game.ENTITY_HEIGHT_HALF;
	}

	public abstract void engage(Player player);

	public abstract void updateState(Game game, float interval);

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public boolean isAlive() {
		return isAlive;
	}
}
