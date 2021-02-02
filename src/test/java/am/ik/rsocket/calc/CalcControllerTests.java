package am.ik.rsocket.calc;

import java.io.File;

import am.ik.rsocket.RscProps;
import am.ik.rsocket.io.CommandRunner;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest("spring.rsocket.server.port=0")
class CalcControllerTests {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RscProps rscProps;

	final CommandRunner commandRunner = new CommandRunner(new File(System.getProperty("user.dir")));

	@Test
	void addJson() throws Exception {
		final String filePath = new ClassPathResource("data/input.json").getFile().getAbsolutePath();
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "add", "-l", filePath, "--dataMimeType", "application/json", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("{\"result\":300}")
				.verifyComplete();
	}
}