package pgdp.robot;

public class Position {

	public double x, y;

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Position(Position copy) {
		this(copy.x, copy.y);
	}

	public Position() {
		this(0, 0);
	}

	public void moveBy(double distance, double direction) {
		x += Math.cos(direction) * distance;
		y += Math.sin(direction) * distance;
	}

	public double distanceTo(Position destination) {
		double dx = destination.x - x;
		double dy = destination.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double directionTo(Position destination) {
		return Math.atan2(destination.y - y, destination.x - x);
	}

	@Override
	public String toString() {
		return "(" + ((int) (x * 100)) / 100.0 + ", " + ((int) (y * 100)) / 100.0 + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Position) {
			Position other = (Position) obj;
			return x == other.x && y == other.y;
		}
		return false;
	}
}