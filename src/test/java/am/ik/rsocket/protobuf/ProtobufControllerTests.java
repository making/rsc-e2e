package am.ik.rsocket.protobuf;

import java.io.File;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;

import am.ik.rsocket.RscProps;
import am.ik.rsocket.io.CommandRunner;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootTest("spring.rsocket.server.port=0")
class ProtobufControllerTests {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RscProps rscProps;

	final CommandRunner commandRunner = new CommandRunner(new File(System.getProperty("user.dir")));

	@Test
	void hello() throws Exception {
		final String filePath = new ClassPathResource("data/request.protobuf").getFile().getAbsolutePath();
		// https://github.com/spring-projects/spring-framework/pull/26487
		final Flux<String> output = this.commandRunner.exec(this.rscProps.command("--route", "protobuf", "--dataMimeType", "application/octet-stream ", "--load", filePath, "tcp://localhost:" + port)).log("rsc");
		StepVerifier.create(output.collect(Collectors.joining(System.lineSeparator()))
				.map(s -> {
					try {
						return HelloResponse.newBuilder()
								.mergeFrom(s.getBytes(UTF_8))
								.build()
								.getReply();
					}
					catch (InvalidProtocolBufferException e) {
						throw new UncheckedIOException(e);
					}
				}))
				.expectNext("Hello World!")
				.verifyComplete();
	}
}