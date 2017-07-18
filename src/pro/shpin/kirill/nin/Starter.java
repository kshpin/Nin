package pro.shpin.kirill.nin;

import pro.shpin.kirill.nin.control.Engine;
import pro.shpin.kirill.nin.model.Game;

public class Starter {

	public static void main(String[] args) {
		Game game = new Game();
		Engine engine;
		try {
			engine = new Engine("GAME", 800, 800, game);
			engine.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
