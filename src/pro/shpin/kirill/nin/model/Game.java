package pro.shpin.kirill.nin.model;

import pro.shpin.kirill.nin.view.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class Game {

	public static final int ENTITY_WIDTH = 20;
	public static final int ENTITY_HEIGHT = 40;

	public static final int ENTITY_WIDTH_HALF = ENTITY_WIDTH/2;
	public static final int ENTITY_HEIGHT_HALF = ENTITY_HEIGHT/2;

	private Player player;

	private List<SectionPrototype> presets;
	private List<Section> sections;

	public static List<Projectile> projectiles;

	private int width;
	private int height;

	public static final int PIXELS_PER_SECTION = 400;

	private float gravityAccel;
	public static float timeScale;

	private static final float NORMAL_TIME_SCALE = 30f;
	private static final float SLOWED_TIME_SCALE = NORMAL_TIME_SCALE/5f;

	private float screenSpeed;
	public float screenPos;

	public int mouseX;
	public int mouseY;

	public boolean leftButtonPressed = false;
	private boolean rightButtonPressed = false;

	private boolean lastUpdateLeftButtonState = false;

	private Random rng;

	public void init(Window window) {
		player = new Player(window.width/2, 200);

		rng = new Random();

		createPresets();
		sections = new ArrayList<>();
		sections.add(presets.get(/*presets.size()-1*/0).build(0));

		projectiles = new ArrayList<>();

		width = window.width;
		height = window.height;

		gravityAccel = 1;
		timeScale = NORMAL_TIME_SCALE;

		screenSpeed = 1.2f;
		screenPos = 0f;

		glfwSetCursorPosCallback(window.getHandle(), (windowHandle, posX, posY) -> {
			mouseX = (int) posX;
			mouseY = window.height - (int) posY;
		});
		glfwSetMouseButtonCallback(window.getHandle(), (windowHandle, button, action, mode) -> {
			leftButtonPressed  = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
			rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
		});
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
				2,
				200f, 200f, 40f, 70f, SectionPrototype.NO_ENEMY,
				528f, 263f, 120f, 80f, SectionPrototype.ARCHER_ENEMY
		}));

		// PRESET 1
		presets.add(new SectionPrototype(new float[] {
				2,
				400f, 50f, 150f, 120f, SectionPrototype.NO_ENEMY,
				650f, 300f, 50f, 60f, SectionPrototype.NO_ENEMY
		}));

		// PRESET 2
		presets.add(new SectionPrototype(new float[] {
				1,
				500f, 100f, 20f, 70f, SectionPrototype.NO_ENEMY
		}));

		// PRESET 3
		presets.add(new SectionPrototype(new float[] {
				6,
				70f, 20f, 100f, 30f, SectionPrototype.NO_ENEMY,
				250f, 75f, 20f, 95f, SectionPrototype.NO_ENEMY,
				400f, 10f, 80f, 60f, SectionPrototype.NO_ENEMY,
				400f, 200f, 85f, 100f, SectionPrototype.NO_ENEMY,
				550f, 20f, 150f, 70f, SectionPrototype.NO_ENEMY,
				650f, 90f, 50f, 250f, SectionPrototype.NO_ENEMY
		}));

		// PRESET 4
		presets.add(new SectionPrototype(new float[] {
				2,
				100, 30, 450, 250, SectionPrototype.NO_ENEMY,
				650, 10, 20, 70, SectionPrototype.NO_ENEMY
		}));

		// PRESET 5
		presets.add(new SectionPrototype(new float[] {
				3,
				200, 10, 200, 150, SectionPrototype.NO_ENEMY,
				480, 170, 30, 150, SectionPrototype.NO_ENEMY,
				600, 0, 150, 50, SectionPrototype.NO_ENEMY
		}));

		// PRESET 6
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

	public Player getPlayer() {
		return player;
	}

	public void input(Window window) {
		if (window.isKeyPressed(GLFW_KEY_SPACE)) System.out.println("Shuriken are being thrown");
	}

	public void update(float interval) {
		float playerScreenPos = player.getPosY() - screenPos;
		if (playerScreenPos*6 > height*5) screenSpeed = player.getSpeedY();
		else if (playerScreenPos*3 > height*2) screenSpeed = 3.6f;
		else screenSpeed = 1.2f;
		screenPos += screenSpeed*timeScale*interval;

		if (player.getPosY() - screenPos < 0) { // TODO set appropriate handle for falling out of the world
			player.setPosY(screenPos);
			player.setAttached(true);
		}

		if (leftButtonPressed) timeScale = SLOWED_TIME_SCALE;
		else timeScale = NORMAL_TIME_SCALE;

		// Mouse released handle
		if (!leftButtonPressed && lastUpdateLeftButtonState) {
			float distanceX = mouseX - player.getPosX();
			float distanceY = mouseY + screenPos - player.getPosY();

			player.setSpeedX(distanceX/10);
			player.setSpeedY(distanceY/10);

			player.setAttached(false);
		}

		// Used later in collision detection
		float prevPosX = player.getPosX();
		float prevPosY = player.getPosY();

		// Update speed and position based on gravity and time scale
		if (!player.isAttached()) {
			player.adjustSpeedY(-gravityAccel*timeScale*interval);
			player.adjustPosX(player.getSpeedX()*timeScale*interval);
			player.adjustPosY(player.getSpeedY()*timeScale*interval);
		} else {
			player.setSpeedX(0);
			player.setSpeedY(0);
		}

		// Update speed and position of projectiles
		for (Projectile projectile : projectiles) {
			projectile.adjustSpeedY(-gravityAccel*timeScale*interval);
			projectile.adjustPosX(projectile.getSpeedX()*timeScale*interval);
			projectile.adjustPosY(projectile.getSpeedY()*timeScale*interval);
		}

		// Add sections if needed
		if (screenPos > (sections.size()-4)*PIXELS_PER_SECTION) {
			sections.add(presets.get(rng.nextInt(presets.size())).build(sections.size()-1));
		}

		for (Section section : sections) {
			for (Wall wall : section.getWalls()) {
				// Check for collision with enemies
				wall.updateState(player, interval);
				if (wall.enemy != null) {
					if (Math.hypot(player.getPosX() - wall.enemy.posX,
							player.getPosY() - wall.enemy.posY) < 50) wall.enemy.engage(player);
					if (!wall.enemy.isAlive) wall.enemy = null;
				}

				// Check for collision with wall
				checkCollision(wall, prevPosX, prevPosY);
			}
		}

		lastUpdateLeftButtonState = leftButtonPressed;
	}

	private void checkCollision(Wall wall, float prevPosX, float prevPosY) {
		if (player.isAttached()) return;

		float centerX = player.getPosX();
		float centerY = player.getPosY();

		if (centerX-ENTITY_WIDTH_HALF < 50) {
			player.setPosX(50 + ENTITY_WIDTH_HALF);
			player.setAttached(true);
			return;
		}
		if (centerX+ENTITY_WIDTH_HALF > width-50) {
			player.setPosX(width-50 - ENTITY_WIDTH_HALF);
			player.setAttached(true);
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
				player.setAttached(true);
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
