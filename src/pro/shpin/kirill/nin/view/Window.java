package pro.shpin.kirill.nin.view;

import org.lwjgl.opengl.GL;
import pro.shpin.kirill.nin.GLUtil;
import pro.shpin.kirill.nin.model.Section;
import pro.shpin.kirill.nin.model.enemies.Enemy;
import pro.shpin.kirill.nin.model.Game;
import pro.shpin.kirill.nin.model.Player;
import pro.shpin.kirill.nin.model.Wall;

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
		drawSections(game.screenPos, game.getSections());
		if (game.leftButtonPressed) drawDirection(game.screenPos, game);
		drawPlayer(game.screenPos, game.getPlayer());

		glfwSwapBuffers(windowHandle);
	}

	private void drawSideWalls() {
		GLUtil.fillRectCenter(0, height/2, 100, height, 0, 0, 0, 0);
		GLUtil.fillRectCenter(width, height/2, 100, height, 0, 0, 0, 0);
	}

	private void drawSections(float offset, List<Section> sections) {
		for (int i = 0; i < sections.size(); i++) {
			if (i < sections.size() - 4) continue;

			Section section = sections.get(i);

			for (Wall wall : section.getWalls()) {
				GLUtil.fillRectCorner(
						wall.x,
						wall.y - offset,
						wall.width,
						wall.height,
						0, 0, 0, 0
				);

				if (wall.enemy != null)
					GLUtil.fillRectCenter(
							wall.enemy.posX,
							wall.enemy.posY - offset,
							Game.ENTITY_WIDTH,
							Game.ENTITY_HEIGHT,
							0, 0.5f, 0.5f, 0
					);
			}
		}
	}

	private void drawDirection(float offset, Game game) {
		Player player = game.getPlayer();
		GLUtil.drawLine(player.getPosX(), player.getPosY() - offset, game.mouseX, game.mouseY, 0, 1, 0);
	}

	private void drawPlayer(float offset, Player player) {
		GLUtil.fillRectCenter(player.getPosX(), player.getPosY() - offset, Game.ENTITY_WIDTH, Game.ENTITY_HEIGHT, 0, 0, 0.5f, 1);
	}

	public boolean isKeyPressed(int keyCode) {
		return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowHandle);
	}
}
