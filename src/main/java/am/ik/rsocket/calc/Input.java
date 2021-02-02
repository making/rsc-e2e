package am.ik.rsocket.calc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Input {
	private final int x;

	private final int y;

	public Input(@JsonProperty("x") int x, @JsonProperty("y") int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int add() {
		return this.x + this.y;
	}

	@Override
	public String toString() {
		return "Input{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}
