package pro.shpin.kirill.nin.model;

import java.util.List;

public class Section {

	private List<Wall> walls;

	public Section(List<Wall> walls) {
		this.walls = walls;
	}

	public List<Wall> getWalls() {
		return walls;
	}
}
