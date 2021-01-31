package am.ik.rsocket.uppercase;

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
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest("spring.rsocket.server.port=0")
@ActiveProfiles("tls")
class UppercaseTlsControllerTests {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RscProps rscProps;

	final CommandRunner commandRunner = new CommandRunner(new File(System.getProperty("user.dir")));


	@Test
	void requestResponse() throws Exception {
		final ClassPathResource resource = new ClassPathResource("local.maki.lol.crt");
		final String caCert = resource.getFile().getAbsolutePath();
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("-r", "uppercase", "-d", "hello", "--trustCert", caCert, "tcp+tls://rsc.local.maki.lol:" + port)).log("rsc");
		StepVerifier.create(output)
				.expectNext("HELLO")
				.verifyComplete();
	}
}
