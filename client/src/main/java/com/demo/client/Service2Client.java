package com.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Service2Client {

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager manager;

    @Value("${service2.url}")
    String service2Url;

    public Service2Client(RestTemplate restTemplate,
                          OAuth2AuthorizedClientManager manager) {
        this.restTemplate = restTemplate;
        this.manager = manager;
    }

    public String fetchData() {
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

        return response.getBody();
    }
}
