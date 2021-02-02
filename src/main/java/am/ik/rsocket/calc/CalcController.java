package am.ik.rsocket.calc;

import reactor.core.publisher.Mono;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class CalcController {
	@MessageMapping("add")
	public Mono<Output> add(@Payload Mono<Input> input) {
		return input.log("input").map(Input::add).map(Output::new).log("output");
	}
}
