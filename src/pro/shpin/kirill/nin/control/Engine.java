package pro.shpin.kirill.nin.control;

import pro.shpin.kirill.nin.model.Game;
import pro.shpin.kirill.nin.view.Window;

public class Engine implements Runnable {

	public static final int TARGET_FPS = 60;
	public static final int TARGET_UPS = 60;

	private final Thread gameLoopThread;
	private final Timer timer;

	private final Window window;

	private final Game game;

	public Engine(String windowTitle, int width, int height, Game game) throws Exception {
		System.setProperty("java.awt.headless", "true");

		this.gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		this.window = new Window(windowTitle, width, height);
		this.game = game;
		this.timer = new Timer();
	}

	public void start() {
		String osName = System.getProperty("os.name");
		if (osName.contains("Mac")) gameLoopThread.run();
		else gameLoopThread.start();
	}

	public void run() {
		try {
			init();
			loop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}

	private void init() throws Exception {
		window.init();
		timer.init();
		game.init(window);
	}

	private void loop() {
		float elapsedTime;
		float accumulator = 0f;
		float interval = 1f/TARGET_UPS;

		boolean running = true;
		while (running && !window.shouldClose()) {
			elapsedTime = timer.getElapsedTime();
			accumulator += elapsedTime;

			input();

			while (accumulator >= interval) {
				update(interval);
				accumulator -= interval;
			}

			render();

			sync();
		}
	}

	private void input() {
		window.updateInput();
		game.updateInput(window);
	}

	private void sync() {
		float loopSlot = 1f/TARGET_FPS;
		double endTime = timer.getLastLoopTime() + loopSlot;

		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {

			}
		}
	}

	private void update(float interval) {
		game.update(interval);
	}

	private void render() {
		window.render(game);
	}

	private void cleanup() {
		game.cleanup();
	}
}
