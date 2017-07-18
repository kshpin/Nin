package pro.shpin.kirill.nin.model;

public class Projectile {

	private float posX;
	private float posY;
	private float speedX;
	private float speedY;

	public Projectile(float posX, float posY, float speedX, float speedY) {
		this.posX = posX;
		this.posY = posY;
		this.speedX = speedX;
		this.speedY = speedY;
	}

	public Projectile(float posX, float posY, double speedX, double speedY) {
		this.posX = posX;
		this.posY = posY;
		this.speedX = (float) speedX;
		this.speedY = (float) speedY;
	}

	public float getPosX() {
		return posX;
	}

	public void adjustPosX(float posX) {
		this.posX += posX;
	}

	public float getPosY() {
		return posY;
	}

	public void adjustPosY(float posY) {
		this.posY += posY;
	}

	public float getSpeedX() {
		return speedX;
	}

	public void adjustSpeedX(float speedX) {
		this.speedX += speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void adjustSpeedY(float speedY) {
		this.speedY += speedY;
	}
}
