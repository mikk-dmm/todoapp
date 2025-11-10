package com.example.todoapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    //現在の天気
    public String getWeather(double lat, double lon) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("exclude", "minutely,hourly,alerts")
                .queryParam("units", "metric")
                .queryParam("lang", "ja")
                .queryParam("appid", apiKey)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }

    //週間予報
    public String getWeeklyWeather(double lat, double lon) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.openweathermap.org/data/2.5/onecall")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("exclude", "minutely,hourly,alerts")
                .queryParam("units", "metric")
                .queryParam("lang", "ja")
                .queryParam("appid", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}