package am.ik.rsocket.uppercase;

import java.io.File;
import java.util.List;

import am.ik.rsocket.io.CommandRunner;
import am.ik.rsocket.RscProps;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest("spring.rsocket.server.port=0")
class UppercaseControllerTests {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RscProps rscProps;

	final CommandRunner commandRunner = new CommandRunner(new File(System.getProperty("user.dir")));

	@Test
	void fireAndForget() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("--fnf", "-r", "uppercase.fnf", "-d", "hello", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.verifyComplete();
	}

	@Test
	void fireAndForgetDebug() {
		final List<String> output = this.commandRunner.exec(this.rscProps.command("--fnf", "-r", "uppercase.fnf", "-d", "hello", "--debug", "tcp://localhost:" + port)).log("rsc").collectList().block();
		assertThat(output).anyMatch(s -> s.contains("Stream ID: 1 Type: REQUEST_FNF"));
	}

	@Test
	void requestResponse() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "uppercase", "-d", "hello", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("HELLO")
				.verifyComplete();
	}

	@Test
	void requestResponseDebug() {
		final List<String> output = this.commandRunner.exec(this.rscProps.command("-r", "uppercase", "-d", "hello", "--debug", "tcp://localhost:" + port)).log("rsc").collectList().block();
		assertThat(output)
				.anyMatch(s -> s.contains("Stream ID: 1 Type: REQUEST_RESPONSE"))
				.anyMatch(s -> s.contains("Stream ID: 1 Type: NEXT_COMPLETE"));
	}

	@Test
	void requestStream() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("--stream", "-r", "uppercase.stream", "-d", "hello", "--limitRate", "3", "--take", "3", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("HELLO")
				.expectNext("HELLO")
				.expectNext("HELLO")
				.verifyComplete();
	}

	@Test
	void requestStreamDebug() {
		final List<String> output = this.commandRunner.exec(this.rscProps.command("--stream", "-r", "uppercase.stream", "-d", "hello", "--limitRate", "3", "--take", "3", "--debug", "tcp://localhost:" + port)).log("rsc").collectList().block();
		assertThat(output)
				.anyMatch(s -> s.contains("Stream ID: 1 Type: REQUEST_STREAM"))
				.matches(list -> list.stream().filter(s -> s.contains("Stream ID: 1 Type: NEXT")).count() == 3)
				.anyMatch(s -> s.contains("Stream ID: 1 Type: CANCEL"));
	}

	@Test
	void requestChannel() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("--channel", "-r", "uppercase.channel", "-d", "hello", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("HELLO")
				.verifyComplete();
	}

	@Test
	void requestChannelDebug() {
		final List<String> output = this.commandRunner.exec(this.rscProps.command("--channel", "-r", "uppercase.channel", "-d", "hello", "--debug", "tcp://localhost:" + port)).log("rsc").collectList().block();
		assertThat(output)
				.anyMatch(s -> s.contains("Stream ID: 1 Type: REQUEST_CHANNEL"))
				.matches(list -> list.stream().filter(s -> s.contains("Stream ID: 1 Type: NEXT")).count() == 1)
				.anyMatch(s -> s.contains("Stream ID: 1 Type: REQUEST_N"))
				.matches(list -> list.stream().filter(s -> s.contains("Stream ID: 1 Type: COMPLETE")).count() == 2);
	}
}
