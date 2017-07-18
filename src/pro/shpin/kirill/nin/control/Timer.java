package pro.shpin.kirill.nin.control;

public class Timer {

	private double lastLoopTime;

	protected void init() {
		lastLoopTime = getTime();
	}

	protected double getTime() {
		return System.nanoTime() / 1000_000_000.0;
	}

	protected float getElapsedTime() {
		double time = getTime();
		float elapsedTime = (float) (time - lastLoopTime);
		lastLoopTime = time;
		return elapsedTime;
	}

	protected double getLastLoopTime() {
		return lastLoopTime;
	}
}
