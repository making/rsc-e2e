package am.ik.rsocket.security;

import java.io.File;

import am.ik.rsocket.RscProps;
import am.ik.rsocket.io.CommandRunner;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest({ "spring.rsocket.server.port=0", "logging.level.am.ik.rsocket.io.CommandRunner=WARN" })
@ActiveProfiles("bearer")
class HelloControllerBearerTests {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RscProps rscProps;

	final CommandRunner commandRunner = new CommandRunner(new File(System.getProperty("user.dir")));

	final String invalidToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.POstGetfAytaZS82wHcjoTyoqhMyxXiWdR7Nn7A29DNSl0EiXLdwJ6xC6AfgZWF1bOsS_TuYI3OG85AmiExREkrS6tDfTQ2B3WXlrr-wp5AokiRbz3_oB4OxG-W9KcEEbDRcZc0nH3L7LzYptiy1PtAylQGxHTWZXtGz4ht0bAecBgmpdgXMguEIcoqPJ1n3pIWk_dUZegpqx0Lka21H6XxUTxiy8OcaarA8zdnPUnV6AmNP3ecFawIFYdvJB_cm-GvpCSbr8G8y_Mllj8f4x9nBH8pQux89_6gUY618iYv7tuPWBFfEbLxtF2pZS6YC1aSfLQxeNe8djT9YjpvRZA";

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
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--authBearer", this.rscProps.jwt(), "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Hello World, " + this.rscProps.getOidcUsername() + "!")
				.verifyComplete();
	}

	@Test
	void authenticationPerStreamSuccessShortcut() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--authBearer", this.rscProps.jwt(), "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Hello World, " + this.rscProps.getOidcUsername() + "!")
				.verifyComplete();
	}

	@Test
	void authenticationPerStreamInvalidToken() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--authBearer", this.invalidToken, "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Error: Failed to validate the token")
				.expectNext("")
				.expectNext("Use --stacktrace option for details.")
				.verifyError();
	}


	@Test
	void authenticationPerConnectionSuccess() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--sm", "bearer:" + this.rscProps.jwt(), "--smmt", "message/x.rsocket.authentication.v0", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Hello World, " + this.rscProps.getOidcUsername() + "!")
				.verifyComplete();
	}

	@Test
	void authenticationPerConnectionSuccessShortcut() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--sm", "bearer:" + this.rscProps.jwt(), "--smmt", "MESSAGE_RSOCKET_AUTHENTICATION", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Hello World, " + this.rscProps.getOidcUsername() + "!")
				.verifyComplete();
	}

	@Test
	void authenticationPerConnectionInvalidToken() {
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "hello", "-d", "World", "--sm", "bearer:" + this.invalidToken, "--smmt", "message/x.rsocket.authentication.v0", "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("Error: Failed to validate the token")
				.expectNext("")
				.expectNext("Use --stacktrace option for details.")
				.verifyError();
	}

}
