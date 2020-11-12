package cmsc420.meeshquest.part2;

public class City {

	private int x, y, radius;
	private String name, color;

	public City(String name, int x, int y, int radius, String color) {
		this.setName(name);
		this.setX(x);
		this.setY(y);
		this.setRadius(radius);
		this.setColor(color);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
