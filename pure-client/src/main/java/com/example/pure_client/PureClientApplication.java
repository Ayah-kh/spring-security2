package com.example.pure_client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PureClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(PureClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientManager auth2AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService
    ) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        oAuth2AuthorizedClientService);

        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        manager.setAuthorizedClientProvider(provider);
        return manager;

    }

    @Bean
    public CommandLineRunner run(
            OAuth2AuthorizedClientManager manager,
            RestTemplate restTemplate,
            @Value("${service2.url}") String service2Url
    ) {

        return args -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("keycloak-client")
                    .principal("authorizeRequest")
                    .build();

            OAuth2AuthorizedClient client = manager.authorize(authorizeRequest);
            String token = client.getAccessToken().getTokenValue();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);

            ResponseEntity<String> response = restTemplate.exchange(service2Url + "/data",
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    String.class);

            System.out.println("Response from service 2: " + response);

        };
    }

}
