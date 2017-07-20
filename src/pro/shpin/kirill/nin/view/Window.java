package pro.shpin.kirill.nin.view;

import org.lwjgl.opengl.GL;
import pro.shpin.kirill.nin.GLUtil;
import pro.shpin.kirill.nin.model.*;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {

	public int width;
	public int height;
	private String title;

	private long windowHandle;

	private int deathScreenTex;
	private int playerTex;
	private int enemyTex;
	private int wallTex;

	public Window(String title, int width, int height) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	public void init() {
		glfwInit();

		windowHandle = glfwCreateWindow(width, height, title, 0, 0);
		glfwMakeContextCurrent(windowHandle);

		GL.createCapabilities();
		glClearColor(0.31f, 0.84f, 0.26f, 1f);

		glEnable(GL_TEXTURE_2D);
		deathScreenTex = GLUtil.loadTexture("/deathScreenBlue.png");
		playerTex = GLUtil.loadTexture("/ninjaPlayer.png");
		enemyTex = GLUtil.loadTexture("/ninjaEnemy.png");
		wallTex = GLUtil.loadTexture("/wallTextures/brickWallTileBlack.jpg");

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, 0, height, -1, 1);
		glMatrixMode(GL_MODELVIEW);
	}

	public long getHandle() {
		return windowHandle;
	}

	public void updateInput() {
		glfwPollEvents();
	}

	public void render(Game game) {
		if (game.getPlayer().isAlive()) {
			glClear(GL_COLOR_BUFFER_BIT);

			drawSideWalls();
			drawSections(game);
			drawProjectiles(game);
			if (game.leftButtonPressed && game.getPlayer().canJump()) drawDirection(game);
			drawPlayer(game);
		} else drawDeathScreen();

		glfwSwapBuffers(windowHandle);
	}

	private void drawSideWalls() {
		GLUtil.tileTexRectCenter(
				0,
				height/2,
				100,
				height,
				0,
				wallTex,
				250,
				150
		);
		GLUtil.tileTexRectCenter(
				width,
				height/2,
				100,
				height,
				0,
				wallTex,
				250,
				150
		);
	}

	private void drawSections(Game game) {
		List<Section> sections = game.getSections();
		for (Section section : sections) {
			for (Wall wall : section.getWalls()) {
				GLUtil.tileTexRectCorner(
						wall.x,
						wall.y - game.screenPos,
						wall.width,
						wall.height,
						0,
						wallTex,
						250,
						150
				);

				if (wall.getEnemy() != null) {
					GLUtil.texRectCenter(
							wall.getEnemy().getPosX(),
							wall.getEnemy().getPosY() - game.screenPos,
							Game.ENTITY_WIDTH,
							Game.ENTITY_HEIGHT,
							0,
							enemyTex,
							1f,
							1f
					);
				}
			}
		}
	}

	private void drawProjectiles(Game game) {
		for (Projectile projectile : game.getProjectiles()) {
			GLUtil.fillRectCenter(
					projectile.getPosX(),
					projectile.getPosY() - game.screenPos,
					Game.PROJECTILE_DIMENSION,
					Game.PROJECTILE_DIMENSION,
					0,
					0.2f,
					0.2f,
					0.2f
			);
		}
	}

	private void drawDirection(Game game) {
		Player player = game.getPlayer();
		GLUtil.drawLine(player.getPosX(), player.getPosY() - game.screenPos, game.mouseX, game.mouseY, 0, 1, 0);
	}

	private void drawPlayer(Game game) {
		Player player = game.getPlayer();
		GLUtil.texRectCenter(
				player.getPosX(),
				player.getPosY() - game.screenPos,
				Game.ENTITY_WIDTH,
				Game.ENTITY_HEIGHT,
				0,
				playerTex,
				1f,
				1f
		);
	}

	private void drawDeathScreen() {
		GLUtil.texRectCenter(
				width/2f,
				height/2f,
				width/4f*3f,
				height/4f*3f,
				0,
				deathScreenTex,
				1f,
				1f
		);
	}

	public boolean isKeyPressed(int keyCode) {
		return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowHandle);
	}
}
