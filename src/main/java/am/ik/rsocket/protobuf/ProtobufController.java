package am.ik.rsocket.protobuf;

import reactor.core.publisher.Mono;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ProtobufController {
	@MessageMapping("protobuf")
	public Mono<HelloResponse> hello(@Payload Mono<HelloRequest> request) {
		return request.map(req -> HelloResponse.newBuilder()
				.setReply("Hello " + req.getGreeting() + "!")
				.build());
	}
}
