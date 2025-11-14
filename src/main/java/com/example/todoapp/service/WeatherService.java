package com.example.todoapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    private static final String WEATHER_ENDPOINT = "https://api.openweathermap.org/data/2.5/weather";

    private final RestTemplate restTemplate;
    private final String apiKey;

    public WeatherService(RestTemplateBuilder restTemplateBuilder,
                            @Value("${weather.api.key:}") String apiKey) {
        this(restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build(), apiKey);
    }

    @Autowired
    WeatherService(RestTemplate restTemplate, @Value("${weather.api.key:}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public ObjectNode getWeather(double lat, double lon) {
        return fetchWeather(lat, lon);
    }

    private ObjectNode fetchWeather(double lat, double lon) {
        if (!StringUtils.hasText(apiKey)) {
            return buildFallback("天気APIキーが未設定のため、天気情報を表示できません。", "API_KEY_MISSING",
                    "API key is missing.");
        }

        String url = UriComponentsBuilder
                .fromHttpUrl(WEATHER_ENDPOINT)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("units", "metric")
                .queryParam("lang", "ja")
                .queryParam("appid", apiKey)
                .toUriString();

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response == null) {
                throw new RestClientException("Empty weather response");
            }

            ObjectNode normalized = JsonNodeFactory.instance.objectNode();
            normalized.put("success", true);
            normalized.put("message", "");
            normalized.put("reason", "OK");
            normalized.put("detail", "");
            normalized.put("lat", lat);
            normalized.put("lon", lon);
            normalized.put("locationName", response.path("name").asText(""));

            ObjectNode current = normalized.putObject("current");
            JsonNode weatherNode = response.path("weather").isArray() && response.path("weather").size() > 0
                    ? response.path("weather").get(0)
                    : null;

            ArrayNode weatherArray = current.putArray("weather");
            ObjectNode weather = weatherArray.addObject();
            if (weatherNode != null && weatherNode.hasNonNull("description")) {
                weather.put("description", weatherNode.get("description").asText());
            } else {
                weather.putNull("description");
            }
            if (weatherNode != null && weatherNode.hasNonNull("icon")) {
                weather.put("icon", weatherNode.get("icon").asText());
            } else {
                weather.putNull("icon");
            }

            JsonNode mainNode = response.path("main");
            if (mainNode.hasNonNull("temp")) {
                current.put("temp", mainNode.get("temp").asDouble());
            } else {
                current.putNull("temp");
            }
            if (mainNode.hasNonNull("humidity")) {
                current.put("humidity", mainNode.get("humidity").asDouble());
            } else {
                current.putNull("humidity");
            }

            JsonNode windNode = response.path("wind");
            if (windNode.hasNonNull("speed")) {
                current.put("wind_speed", windNode.get("speed").asDouble());
            } else {
                current.putNull("wind_speed");
            }

            return normalized;
        } catch (RestClientResponseException ex) {
            log.warn("Weather API responded with an error for lat={}, lon={}", lat, lon, ex);
            return buildFallback(
                    resolveMessageForStatus(ex.getStatusCode()),
                    resolveReasonForStatus(ex.getStatusCode()),
                    buildDetailFromException(ex)
            );
        } catch (RestClientException ex) {
            log.warn("Failed to fetch weather for lat={}, lon={}", lat, lon, ex);
            return buildFallback(
                    "天気情報の取得に失敗しました。時間をおいて再度お試しください。",
                    "REMOTE_API_ERROR",
                    StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : "Unexpected client error."
            );
        }
    }

    private ObjectNode buildFallback(String message, String reason, String detail) {
        ObjectNode fallback = JsonNodeFactory.instance.objectNode();
        fallback.put("success", false);
        fallback.put("message", message);
        fallback.put("reason", reason);
        fallback.put("detail", detail != null ? detail : "");
        fallback.putNull("lat");
        fallback.putNull("lon");

        ObjectNode current = fallback.putObject("current");
        ArrayNode weatherArray = current.putArray("weather");
        weatherArray.addObject()
                .put("description", message)
                .putNull("icon");
        current.putNull("temp");
        current.putNull("humidity");
        current.putNull("wind_speed");
        return fallback;
    }

    private String resolveMessageForStatus(HttpStatusCode statusCode) {
        if (statusCode == null) {
            return "天気情報の取得中に不明なエラーが発生しました。";
        }
        int status = statusCode.value();
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return "天気APIキーが無効か期限切れです。設定を確認してください。";
        }
        if (status == HttpStatus.FORBIDDEN.value()) {
            return "天気APIにアクセスできません。APIキーの権限を確認してください。";
        }
        if (status == HttpStatus.TOO_MANY_REQUESTS.value()) {
            return "天気APIの呼び出し制限を超えています。時間をおいて再試行してください。";
        }
        if (status >= 500) {
            return "天気API側で障害が発生しています。時間をおいて再度お試しください。";
        }
        return "天気情報の取得に失敗しました。詳細を確認してください。";
    }

    private String resolveReasonForStatus(HttpStatusCode statusCode) {
        if (statusCode == null) {
            return "UNKNOWN";
        }
        int status = statusCode.value();
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return "API_KEY_INVALID";
        }
        if (status == HttpStatus.FORBIDDEN.value()) {
            return "API_FORBIDDEN";
        }
        if (status == HttpStatus.TOO_MANY_REQUESTS.value()) {
            return "RATE_LIMIT";
        }
        if (status >= 500) {
            return "REMOTE_API_DOWN";
        }
        return "REMOTE_API_ERROR";
    }

    private String buildDetailFromException(RestClientResponseException ex) {
        StringBuilder detail = new StringBuilder(ex.getStatusCode().toString());
        if (StringUtils.hasText(ex.getResponseBodyAsString())) {
            detail.append(" / ").append(ex.getResponseBodyAsString().trim());
        }
        return detail.toString();
    }
}
