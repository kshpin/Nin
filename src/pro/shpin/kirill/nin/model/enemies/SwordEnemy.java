package pro.shpin.kirill.nin.model.enemies;

import pro.shpin.kirill.nin.model.Game;
import pro.shpin.kirill.nin.model.Player;
import pro.shpin.kirill.nin.model.Wall;

public class SwordEnemy extends Enemy {

	private static final float STRENGTH = 20;

	private float origPosX;

	private float wallWidth;

	private static final float MOVE_SPEED = 0.5f;
	private int moveDirection = 1;

	private byte hitCounter = 0;
	private float updatesSinceLastHit = 0;

	public SwordEnemy(float x, float y, float wallWidth) {
		super(x + Game.ENTITY_WIDTH_HALF, y);
		this.origPosX = posX;
		this.wallWidth = wallWidth - Game.ENTITY_WIDTH;
	}

	@Override
	public void engage(Player player) {
		if (updatesSinceLastHit < 150f) return;
		updatesSinceLastHit = 0f;

		if (hitCounter == 2) isAlive = false;
		else {
			player.setAttached(false);
			double theta = Math.atan2(player.getPosY()-posY, player.getPosX()-posX);
			player.setSpeedX(Math.cos(theta) * STRENGTH);
			player.setSpeedY(Math.sin(theta) * STRENGTH);

			hitCounter++;
		}
	}

	@Override
	public void updateState(Player player, float interval) {
		updatesSinceLastHit += 600f * Game.timeScale * interval;
		posX += MOVE_SPEED * moveDirection;
		if (posX > origPosX + wallWidth) {
			posX = origPosX + wallWidth;
			moveDirection = -1;
		}
		if (posX < origPosX) {
			posX = origPosX;
			moveDirection = 1;
		}
	}
}
