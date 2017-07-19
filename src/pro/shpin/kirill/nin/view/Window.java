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
		glClearColor(1f, 0.79f, 0.28f, 1f);

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
		glClear(GL_COLOR_BUFFER_BIT);

		drawSideWalls();
		drawSections(game);
		drawProjectiles(game);
		if (game.leftButtonPressed) drawDirection(game);
		drawPlayer(game);

		glfwSwapBuffers(windowHandle);
	}

	private void drawSideWalls() {
		GLUtil.fillRectCenter(0, height/2, 100, height, 0, 0, 0, 0);
		GLUtil.fillRectCenter(width, height/2, 100, height, 0, 0, 0, 0);
	}

	private void drawSections(Game game) {
		List<Section> sections = game.getSections();
		for (Section section : sections) {
			for (Wall wall : section.getWalls()) {
				GLUtil.fillRectCorner(
						wall.x,
						wall.y - game.screenPos,
						wall.width,
						wall.height,
						0, 0, 0, 0
				);

				if (wall.getEnemy() != null)
					GLUtil.fillRectCenter(
							wall.getEnemy().getPosX(),
							wall.getEnemy().getPosY() - game.screenPos,
							Game.ENTITY_WIDTH,
							Game.ENTITY_HEIGHT,
							0, 0.5f, 0.5f, 0
					);
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
		GLUtil.fillRectCenter(
				player.getPosX(),
				player.getPosY() - game.screenPos,
				Game.ENTITY_WIDTH,
				Game.ENTITY_HEIGHT,
				0,
				0,
				0.5f,
				1
		);
	}

	public boolean isKeyPressed(int keyCode) {
		return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowHandle);
	}
}
