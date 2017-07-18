package pro.shpin.kirill.nin.model.enemies;

import pro.shpin.kirill.nin.model.Game;
import pro.shpin.kirill.nin.model.Player;
import pro.shpin.kirill.nin.model.Projectile;

public class ArcherEnemy extends Enemy {

	private static final float STRENGTH = 20;
	private float updatesSinceLastShot = 0f;

	public ArcherEnemy(float x, float y) {
		super(x, y);
	}

	@Override
	public void engage(Player player) {
		isAlive = false;
	}

	@Override
	public void updateState(Player player, float interval) {
		updatesSinceLastShot += 600f * Game.timeScale * interval;
		if (updatesSinceLastShot < 150) return;
		updatesSinceLastShot = 0f;

		double theta = Math.atan2(player.getPosY()-posY, player.getPosX()-posX);
		Game.projectiles.add(new Projectile(posX, posY, Math.cos(theta) * STRENGTH, Math.sin(theta) * STRENGTH));
	}
}
