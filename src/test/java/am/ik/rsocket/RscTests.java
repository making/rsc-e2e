package am.ik.rsocket;

import java.io.File;

import am.ik.rsocket.io.CommandRunner;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest("spring.rsocket.server.port=0")
@TestMethodOrder(OrderAnnotation.class)
class RscTests {
	@Autowired
	RscProps rscProps;

	final CommandRunner commandRunner = new CommandRunner(new File(System.getProperty("user.dir")));

	@Test
	@Order(0)
	void version() {
		this.commandRunner.exec(this.rscProps.command("--version")).log("rsc").collectList().block();
	}

	@Test
	@Order(1)
	void showSystemProperties() {
		this.commandRunner.exec(this.rscProps.command("--showSystemProperties")).log("rsc").collectList().block();
	}
}
