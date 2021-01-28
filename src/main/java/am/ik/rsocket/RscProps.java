package am.ik.rsocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ConfigurationProperties(prefix = "rsc")
@ConstructorBinding
public class RscProps {
	private final String path;

	private final String oidcIssuerUrl;

	private final String oidcClientId;

	private final String oidcClientSecret;

	private final String oidcUsername;

	private final String oidcPassword;

	private final String oidcUsernameClaimName;

	private String jwt = null;

	public RscProps(String path, String oidcIssuerUrl, String oidcClientId, String oidcClientSecret, String oidcUsername, String oidcPassword, String oidcUsernameClaimName) {
		this.path = path;
		this.oidcIssuerUrl = oidcIssuerUrl;
		this.oidcClientId = oidcClientId;
		this.oidcClientSecret = oidcClientSecret;
		this.oidcUsername = oidcUsername;
		this.oidcPassword = oidcPassword;
		this.oidcUsernameClaimName = oidcUsernameClaimName;
	}

	public String[] getPath() {
		return path.split(" ");
	}

	public String getOidcIssuerUrl() {
		return oidcIssuerUrl;
	}

	public String getOidcClientId() {
		return oidcClientId;
	}

	public String getOidcClientSecret() {
		return oidcClientSecret;
	}

	public String getOidcUsername() {
		return oidcUsername;
	}

	public String getOidcPassword() {
		return oidcPassword;
	}

	public String getOidcUsernameClaimName() {
		return oidcUsernameClaimName;
	}

	public String getOpenIdConfigurationUrl() {
		return this.oidcIssuerUrl + "/.well-known/openid-configuration";
	}

	public String jwt() {
		if (this.jwt == null) {
			final RestTemplate restTemplate = new RestTemplate();
			final JsonNode config = restTemplate.getForObject(this.getOpenIdConfigurationUrl(), JsonNode.class);
			final String tokenEndpoint = config.get("token_endpoint").asText();
			final RequestEntity<HttpHeaders> tokenRequest = RequestEntity.post(tokenEndpoint)
					.headers(httpHeaders -> httpHeaders.setBasicAuth(this.getOidcClientId(), this.getOidcClientSecret()))
					.body(new HttpHeaders() {
						{
							add("grant_type", "password");
							add("username", getOidcUsername());
							add("password", getOidcPassword());
							add("scope", "openid");
						}
					});
			final ResponseEntity<JsonNode> token = restTemplate.exchange(tokenRequest, JsonNode.class);
			this.jwt = token.getBody().get("id_token").asText();
		}
		return this.jwt;
	}

	public String[] command(String... command) {
		final List<String> list = new ArrayList<>();
		list.addAll(Arrays.asList(this.getPath()));
		list.addAll(Arrays.asList(command));
		return list.toArray(new String[0]);
	}
}
