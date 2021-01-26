package am.ik.rsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class UppercaseController {
	private final Logger log = LoggerFactory.getLogger(UppercaseController.class);

	@MessageMapping("uppercase.fnf")
	public Mono<Void> fnf(@Payload String message) {
		final String upperCase = message.toUpperCase();
		return Mono.just(upperCase).log("uppercase.fnf").then();
	}

	@MessageMapping({ "uppercase", "uppercase.rr" })
	public Mono<String> rr(@Payload String message) {
		final String upperCase = message.toUpperCase();
		return Mono.just(upperCase).log("uppercase.rr");
	}

	@MessageMapping("uppercase.stream")
	public Flux<String> stream(@Payload String message) {
		final String upperCase = message.toUpperCase();
		return Mono.just(upperCase).repeat().log("uppercase.stream");
	}

	@MessageMapping("uppercase.channel")
	public Flux<String> channel(@Payload Flux<String> messages) {
		return messages.map(message -> {
			final String upperCase = message.toUpperCase();
			log.info("channel: {}", upperCase);
			return upperCase;
		}).log("uppercase.channel");
	}
}
