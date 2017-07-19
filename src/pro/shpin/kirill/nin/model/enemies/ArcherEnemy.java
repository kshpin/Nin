package pro.shpin.kirill.nin.model.enemies;

import pro.shpin.kirill.nin.model.Game;
import pro.shpin.kirill.nin.model.Player;
import pro.shpin.kirill.nin.model.Projectile;

public class ArcherEnemy extends Enemy {

	private float updatesSinceLastShot = 0f;

	public ArcherEnemy(float x, float y) {
		super(x, y);
	}

	@Override
	public void engage(Player player) {
		isAlive = false;
	}

	@Override
	public void updateState(Game game, float interval) {
		updatesSinceLastShot += 600f * game.timeScale * interval;
		if (updatesSinceLastShot < 10000) return;
		updatesSinceLastShot = 0f;

		Player player = game.getPlayer();

		double theta = Math.atan2(player.getPosY()-posY, player.getPosX()-posX);
		game.getProjectiles().add(new Projectile(
				posX,
				posY,
				Math.cos(theta) * game.projectileSpeed,
				Math.sin(theta) * game.projectileSpeed,
				false
		));
	}
}
