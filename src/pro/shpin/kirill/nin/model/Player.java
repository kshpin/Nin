package pro.shpin.kirill.nin.model;

public class Player {

	private float posX;
	private float posY;

	private float speedX;
	private float speedY;

	private boolean attached;

	public Player(float x, float y) {
		this.posX = x;
		this.posY = y;
		this.speedX = 0;
		this.speedY = 0;
		this.attached = false;
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public void adjustPosX(float posX) {
		this.posX += posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public void adjustPosY(float posY) {
		this.posY += posY;
	}

	public float getSpeedX() {
		return speedX;
	}

	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	public void setSpeedX(double speedX) {
		this.speedX = (float) speedX;
	}

	public void adjustSpeedX(float speedX) {
		this.speedX += speedX;
	}

	public void adjustSpeedX(double speedX) {
		this.speedX += (float) speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}

	public void setSpeedY(double speedY) {
		this.speedY = (float) speedY;
	}

	public void adjustSpeedY(float speedX) {
		this.speedX += speedX;
	}

	public void adjustSpeedY(double speedX) {
		this.speedX += (float) speedX;
	}

	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

	public void adjustCorner(int cornerId) {
		if (cornerId == 0) {
			posX += Game.ENTITY_WIDTH_HALF;
			posY += Game.ENTITY_HEIGHT_HALF;
		} else if (cornerId == 1) {
			posX -= Game.ENTITY_WIDTH_HALF;
			posY += Game.ENTITY_HEIGHT_HALF;
		} else if (cornerId == 2) {
			posX -= Game.ENTITY_WIDTH_HALF;
			posY -= Game.ENTITY_HEIGHT_HALF;
		} else if (cornerId == 3) {
			posX += Game.ENTITY_WIDTH_HALF;
			posY -= Game.ENTITY_HEIGHT_HALF;
		}
	}
}
