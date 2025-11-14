package com.example.todoapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class WeatherServiceTest {

    private WeatherService weatherService;
    private MockRestServiceServer server;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        weatherService = new WeatherService(restTemplate, "test-key");
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getWeatherReturnsNormalizedCurrentData() {
        double lat = 35.0;
        double lon = 139.0;
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("units", "metric")
                .queryParam("lang", "ja")
                .queryParam("appid", "test-key")
                .toUriString();
        String payload = """
                {
                  "name": "東京",
                  "weather": [{"description": "晴れ", "icon": "01d"}],
                  "main": {"temp": 22.5, "humidity": 61},
                  "wind": {"speed": 3.4}
                }
                """;

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        JsonNode result = weatherService.getWeather(lat, lon);

        assertThat(result.path("success").asBoolean()).isTrue();
        assertThat(result.path("current").path("temp").asDouble()).isEqualTo(22.5);
        assertThat(result.path("current").path("humidity").asDouble()).isEqualTo(61.0);
        assertThat(result.path("current").path("wind_speed").asDouble()).isEqualTo(3.4);
        assertThat(result.path("current").path("weather").get(0).path("icon").asText()).isEqualTo("01d");
        assertThat(result.path("locationName").asText()).isEqualTo("東京");
        assertThat(result.path("detail").asText()).isEmpty();
        server.verify();
    }

    @Test
    void fallbackResponseReturnedWhenApiKeyMissing() {
        weatherService = new WeatherService(restTemplate, "");
        server = MockRestServiceServer.createServer(restTemplate);

        JsonNode result = weatherService.getWeather(0, 0);

        assertThat(result.path("success").asBoolean()).isFalse();
        assertThat(result.path("message").asText()).contains("天気APIキー");
        assertThat(result.path("current").path("weather")).hasSize(1);
        assertThat(result.path("current").path("wind_speed").isNull()).isTrue();
        assertThat(result.path("detail").asText()).isEqualTo("API key is missing.");
    }

    @Test
    void fallbackContainsReasonWhenRemoteReturnsUnauthorized() {
        double lat = 35.0;
        double lon = 135.0;
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("units", "metric")
                .queryParam("lang", "ja")
                .queryParam("appid", "test-key")
                .toUriString();

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"Invalid API key\"}"));

        JsonNode result = weatherService.getWeather(lat, lon);

        assertThat(result.path("success").asBoolean()).isFalse();
        assertThat(result.path("reason").asText()).isEqualTo("API_KEY_INVALID");
        assertThat(result.path("message").asText()).contains("無効");
        assertThat(result.path("detail").asText()).contains("401");
    }
}
