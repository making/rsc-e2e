package am.ik.rsocket.io;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "rsc")
@ConstructorBinding
public class RscProps {
	private final String path;

	public RscProps(String path) {
		this.path = path;
	}

	public String[] getPath() {
		return path.split(" ");
	}
}
