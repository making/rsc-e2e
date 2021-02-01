package am.ik.rsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RscE2eApplication {

	public static void main(String[] args) {
		SpringApplication.run(RscE2eApplication.class, args);
	}

	@Bean
	public RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
		return builder -> builder
				.decoder(new ProtobufDecoder())
				.encoder(new ProtobufEncoder())
				.build();
	}
}
