package am.ik.rsocket.calc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Output {
	private Number result;

	public Output(@JsonProperty("result") Number result) {
		this.result = result;
	}

	public Number getResult() {
		return result;
	}

	@Override
	public String toString() {
		return "Output{" +
				"result=" + result +
				'}';
	}
}
