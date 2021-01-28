package am.ik.rsocket.security;

import java.io.File;

import am.ik.rsocket.io.CommandRunner;
import am.ik.rsocket.RscProps;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest("spring.rsocket.server.port=0")
class HelloControllerTests {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RscProps rscProps;

	final CommandRunner commandRunner = new CommandRunner(new File(System.getProperty("user.dir")));

	@Test
	void authenticationAccessDenied() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Error: Access Denied")
				.expectNext("")
				.expectNext("Use --stacktrace option for details.")
				.verifyError();
	}

	@Test
	void authenticationPerStreamSuccess() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--authSimple", "user:password", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Hello World, user!")
				.verifyComplete();
	}

	@Test
	void authenticationPerStreamSuccessShortcut() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "-u", "user:password", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Hello World, user!")
				.verifyComplete();
	}

	@Test
	void authenticationPerStreamInvalidCredentials() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--authSimple", "user:user", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Error: Invalid Credentials")
				.expectNext("")
				.expectNext("Use --stacktrace option for details.")
				.verifyError();
	}


	@Test
	void authenticationPerConnectionSuccess() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--sm", "simple:user:password", "--smmt", "message/x.rsocket.authentication.v0", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Hello World, user!")
				.verifyComplete();
	}

	@Test
	void authenticationPerConnectionSuccessShortcut() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--sm", "simple:user:password", "--smmt", "MESSAGE_RSOCKET_AUTHENTICATION", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Hello World, user!")
				.verifyComplete();
	}

	@Test
	void authenticationPerConnectionInvalidCredentials() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--sm", "simple:user:user", "--smmt", "message/x.rsocket.authentication.v0", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Error: Invalid Credentials")
				.expectNext("")
				.expectNext("Use --stacktrace option for details.")
				.verifyError();
	}
}
