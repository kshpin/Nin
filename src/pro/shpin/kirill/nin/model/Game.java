package pro.shpin.kirill.nin.model;

import pro.shpin.kirill.nin.model.enemies.Enemy;
import pro.shpin.kirill.nin.view.Window;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {

	public static final int ENTITY_WIDTH = 20;
	public static final int ENTITY_HEIGHT = 40;

	public static final int ENTITY_WIDTH_HALF = ENTITY_WIDTH/2;
	public static final int ENTITY_HEIGHT_HALF = ENTITY_HEIGHT/2;

	public static final int PROJECTILE_DIMENSION = 10;

	private static final float GRAVITY_ACCEL = 1f;

	private static final float NORMAL_TIME_SCALE = 30f;
	private static final float SLOWED_TIME_SCALE = NORMAL_TIME_SCALE/5f;

	static final int PIXELS_PER_SECTION = 400;

	private Player player;

	private List<SectionPrototype> presets;
	private List<Section> sections;
	private int numSections;

	private List<Projectile> projectiles;

	public float projectileSpeed = 20;

	private int width;
	private int height;

	public float timeScale = NORMAL_TIME_SCALE;

	private float screenSpeed;
	public float screenPos;

	public int mouseX;
	public int mouseY;

	private boolean firstAttach;

	public boolean leftButtonPressed = false;
	private boolean lastUpdateLeftButtonState = false;

	private boolean spacePressed = false;
	private boolean lastUpdateSpaceState = false;

	private Random rng;

	public void init(Window window) {
		createPresets();

		rng = new Random();

		width = window.width;
		height = window.height;

		glfwSetCursorPosCallback(window.getHandle(), (windowHandle, posX, posY) -> {
			mouseX = (int) posX;
			mouseY = height - (int) posY;
		});
		glfwSetMouseButtonCallback(window.getHandle(), (windowHandle, button, action, mode) -> {
			leftButtonPressed  = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
		});

		reinit();
	}

	private void reinit() {
		player = new Player(width/2f, 50f + ENTITY_HEIGHT_HALF);

		numSections = 0;
		sections = new ArrayList<>();
		sections.add(presets.get(0).build(numSections++));

		projectiles = new ArrayList<>();

		screenSpeed = 1.2f;
		screenPos = 0f;

		firstAttach = true;
	}

	private void createPresets() {
		/*
		 * Information for the section prototypes in the following format:
		 *
		 * NUM_OF_WALLS,
		 * WALL_X, WALL_Y, WALL_WIDTH, WALL_HEIGHT, ENEMY_TYPE
		 * WALL_X, WALL_Y, WALL_WIDTH, WALL_HEIGHT, ENEMY_TYPE
		 * ...
		 *
		 */

		presets = new ArrayList<>();

		// PRESET 0
		presets.add(new SectionPrototype(new float[] {
				1,
				150f, 0f, 500f, 50f, SectionPrototype.NO_ENEMY
		}));

		// PRESET 1
		presets.add(new SectionPrototype(new float[] {
				2,
				200f, 200f, 40f, 70f, SectionPrototype.NO_ENEMY,
				528f, 263f, 120f, 80f, SectionPrototype.ARCHER_ENEMY
		}));

		// PRESET 2
		presets.add(new SectionPrototype(new float[] {
				2,
				400f, 50f, 150f, 120f, SectionPrototype.NO_ENEMY,
				650f, 300f, 50f, 60f, SectionPrototype.NO_ENEMY
		}));

		// PRESET 3
		presets.add(new SectionPrototype(new float[] {
				1,
				500f, 100f, 20f, 70f, SectionPrototype.NO_ENEMY
		}));

		// PRESET 4
		presets.add(new SectionPrototype(new float[] {
				6,
				70f, 20f, 100f, 30f, SectionPrototype.NO_ENEMY,
				250f, 75f, 20f, 95f, SectionPrototype.NO_ENEMY,
				400f, 10f, 80f, 60f, SectionPrototype.NO_ENEMY,
				400f, 200f, 85f, 100f, SectionPrototype.NO_ENEMY,
				550f, 20f, 150f, 70f, SectionPrototype.NO_ENEMY,
				650f, 90f, 50f, 250f, SectionPrototype.NO_ENEMY
		}));

		// PRESET 5
		presets.add(new SectionPrototype(new float[] {
				2,
				100, 30, 450, 250, SectionPrototype.NO_ENEMY,
				650, 10, 20, 70, SectionPrototype.NO_ENEMY
		}));

		// PRESET 6
		presets.add(new SectionPrototype(new float[] {
				3,
				200, 10, 200, 150, SectionPrototype.NO_ENEMY,
				480, 170, 30, 150, SectionPrototype.NO_ENEMY,
				600, 0, 150, 50, SectionPrototype.NO_ENEMY
		}));

		// PRESET 7
		presets.add(new SectionPrototype(new float[] {
				4,
				50, 300, 150, 30, SectionPrototype.NO_ENEMY,
				250, 75, 20, 95, SectionPrototype.NO_ENEMY,
				400, 10, 80, 60, SectionPrototype.NO_ENEMY,
				400, 200, 85, 100, SectionPrototype.NO_ENEMY
		}));
	}

	public List<Section> getSections() {
		return sections;
	}

	public List<Projectile> getProjectiles() {
		return projectiles;
	}

	public Player getPlayer() {
		return player;
	}

	public void updateInput(Window window) {
		spacePressed = window.isKeyPressed(GLFW_KEY_SPACE);
	}

	public void update(float interval) {
		// Move the screen up
		float playerScreenPos = player.getPosY() - screenPos;
		if (playerScreenPos*6 > height*5) screenSpeed = player.getSpeedY();
		else if (playerScreenPos*3 > height*2) screenSpeed = 3.6f;
		else screenSpeed = 1.2f;
		if (!firstAttach) screenPos += screenSpeed*timeScale*interval;

		if (player.getPosY() - screenPos < 0) {
			player.die();
		}

		processInput();

		updateProjectiles(interval);

		// Used later in collision detection
		float prevPosX = player.getPosX();
		float prevPosY = player.getPosY();

		// Update player speed and position based on gravity and time scale
		if (!player.isAttached()) {
			firstAttach = false;
			player.adjustSpeedY(-GRAVITY_ACCEL *timeScale*interval);
			player.adjustPosX(player.getSpeedX()*timeScale*interval);
			player.adjustPosY(player.getSpeedY()*timeScale*interval);
		} else {
			player.setSpeedX(0);
			player.setSpeedY(0);
		}

		// Add sections if needed
		if (screenPos > (sections.size()-4)*PIXELS_PER_SECTION) {
			sections.add(presets.get(rng.nextInt(presets.size()-1)+1).build(numSections++));
			//if (sections.size() > 5) sections.remove(0);
		}

		updateSections(interval, prevPosX, prevPosY);

		lastUpdateLeftButtonState = leftButtonPressed;
		lastUpdateSpaceState = spacePressed;
	}

	private void processInput() {
		if (!player.isAlive() && spacePressed) reinit();

		if (player.canJump()) {
			if (leftButtonPressed) timeScale = SLOWED_TIME_SCALE;
			else timeScale = NORMAL_TIME_SCALE;

			// Mouse released handle
			if (!leftButtonPressed && lastUpdateLeftButtonState) {
				player.jump();

				float distanceX = mouseX - player.getPosX();
				float distanceY = mouseY + screenPos - player.getPosY();

				player.setSpeedX(distanceX/10f);
				player.setSpeedY(distanceY/10f);
			}
		}

		// Space press handle
		if (spacePressed && !lastUpdateSpaceState) {
			double theta = Math.atan2(mouseY-player.getPosY()+screenPos, mouseX-player.getPosX());

			projectiles.add(new Projectile(
					player.getPosX(),
					player.getPosY(),
					Math.cos(theta) * projectileSpeed,
					Math.sin(theta) * projectileSpeed,
					true
			));
		}
	}

	private void updateProjectiles(float interval) {
		for (Iterator<Projectile> iter = projectiles.listIterator(); iter.hasNext(); ) {
			Projectile projectile = iter.next();
			//projectile.adjustSpeedY(-GRAVITY_ACCEL*timeScale*interval);
			projectile.adjustPosX(projectile.getSpeedX()*timeScale*interval);
			projectile.adjustPosY(projectile.getSpeedY()*timeScale*interval);

			if (projectile.getPosY() - screenPos < 0
					|| projectile.getPosY() - screenPos > height
					|| projectile.getPosX() < 0
					|| projectile.getPosX() > width) iter.remove();

			if (projectile.getPosX() > player.getPosX() - ENTITY_WIDTH_HALF
			 && projectile.getPosX() < player.getPosX() + ENTITY_WIDTH_HALF
			 && projectile.getPosY() > player.getPosY() - ENTITY_HEIGHT_HALF
			 && projectile.getPosY() < player.getPosY() + ENTITY_HEIGHT_HALF
			 && !projectile.isShotByPlayer()) {
				player.die();
			}
		}
	}

	private void updateSections(float interval, float prevPosX, float prevPosY) {
		for (Section section : sections) {
			for (Wall wall : section.getWalls()) {
				// Check for collision with enemies
				wall.updateState(this, interval);

				Enemy enemy = wall.getEnemy();

				// Remove enemy if hit by player's projectile
				enemyNotNull : if (enemy != null) {
					for (Projectile projectile : projectiles) {
						if (projectile.getPosX() > enemy.getPosX() - ENTITY_WIDTH_HALF
						 && projectile.getPosX() < enemy.getPosX() + ENTITY_WIDTH_HALF
						 && projectile.getPosY() > enemy.getPosY() - ENTITY_HEIGHT_HALF
						 && projectile.getPosY() < enemy.getPosY() + ENTITY_HEIGHT_HALF
						 && projectile.isShotByPlayer()) {
							wall.nullifyEnemy();
							break enemyNotNull;
						}
					}

					if (Math.hypot(player.getPosX() - enemy.getPosX(),
							player.getPosY() - enemy.getPosY()) < 50) enemy.engage(player);
					if (!enemy.isAlive()) wall.nullifyEnemy();
				}

				// Check for collision with wall
				checkCollision(wall, prevPosX, prevPosY);

				// Remove any projectiles that are in contact with walls
				projectiles.removeIf(projectile -> projectile.getPosX() > wall.x
												&& projectile.getPosX() < wall.x + wall.width
												&& projectile.getPosY() > wall.y
												&& projectile.getPosY() < wall.y + wall.height);
			}
		}
	}

	private void checkCollision(Wall wall, float prevPosX, float prevPosY) {
		if (player.isAttached()) return;

		float centerX = player.getPosX();
		float centerY = player.getPosY();

		if (centerX-ENTITY_WIDTH_HALF < 50) {
			player.setPosX(50 + ENTITY_WIDTH_HALF);
			player.attach();
			return;
		}
		if (centerX+ENTITY_WIDTH_HALF > width-50) {
			player.setPosX(width-50 - ENTITY_WIDTH_HALF);
			player.attach();
			return;
		}

		float referencePosX, referencePosY;
		float prevReferencePosX, prevReferencePosY;
		for (int i = 0; i < 4; i++) {
			if (i == 0) { // BOTTOM LEFT
				referencePosX = centerX - ENTITY_WIDTH_HALF;
				referencePosY = centerY - ENTITY_HEIGHT_HALF;
				prevReferencePosX = prevPosX - ENTITY_WIDTH_HALF;
				prevReferencePosY = prevPosY - ENTITY_HEIGHT_HALF;
			} else if (i == 1) { // BOTTOM RIGHT
				referencePosX = centerX + ENTITY_WIDTH_HALF;
				referencePosY = centerY - ENTITY_HEIGHT_HALF;
				prevReferencePosX = prevPosX + ENTITY_WIDTH_HALF;
				prevReferencePosY = prevPosY - ENTITY_HEIGHT_HALF;
			} else if (i == 2) { // TOP RIGHT
				referencePosX = centerX + ENTITY_WIDTH_HALF;
				referencePosY = centerY + ENTITY_HEIGHT_HALF;
				prevReferencePosX = prevPosX + ENTITY_WIDTH_HALF;
				prevReferencePosY = prevPosY + ENTITY_HEIGHT_HALF;
			} else { // TOP LEFT
				referencePosX = centerX - ENTITY_WIDTH_HALF;
				referencePosY = centerY + ENTITY_HEIGHT_HALF;
				prevReferencePosX = prevPosX - ENTITY_WIDTH_HALF;
				prevReferencePosY = prevPosY + ENTITY_HEIGHT_HALF;
			}

			if (referencePosX > wall.x
			 && referencePosX < wall.x + wall.width
			 && referencePosY > wall.y
			 && referencePosY < wall.y + wall.height) {
				float[] intersection = getPointOnRect(
						prevReferencePosX,
						prevReferencePosY,
						referencePosX,
						referencePosY,
						wall.x,
						wall.y,
						wall.x + wall.width,
						wall.y + wall.height
				);

				player.setPosX(intersection[0]);
				player.setPosY(intersection[1]);
				player.adjustCorner(i);
				player.attach();
				return;
			}
		}
	}

	private float[] getPointOnRect(float x1, float y1, float x2, float y2, float minX, float minY, float maxX, float maxY) {
		// BOTTOM LEFT CORNER
		if (x1 <= minX && y1 <= minY) return new float[]{minX, minY};

		// BOTTOM RIGHT CORNER
		if (x1 >= maxX && y1 <= minY) return new float[]{maxX, minY};

		// TOP RIGHT CORNER
		if (x1 >= maxX && y1 >= maxY) return new float[]{maxX, maxY};

		// TOP LEFT CORNER
		if (x1 <= minX && y1 >= maxY) return new float[]{minX, maxY};

		// LEFT SIDE
		if (x1 <= minX) return new float[] {minX, (y2-y1)/2 + y1};

		// BOTTOM SIDE
		if (y1 <= minY) return new float[] {(x2-x1)/2 + x1, minY};

		// RIGHT SIDE
		if (x1 >= maxX) return new float[] {maxX, (y2-y1)/2 + y1};

		// TOP SIDE
		if (y1 >= maxY) return new float[] {(x2-x1)/2 + x1, maxY};

		return new float[] {0, 0};
	}

	public void cleanup() {

	}
}
