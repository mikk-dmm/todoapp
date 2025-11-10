package com.example.todoapp.controller.api;

import com.example.todoapp.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // 現在の天気
    @GetMapping("/api/weather")
    public String getWeather(@RequestParam double lat, @RequestParam double lon) {
        return weatherService.getWeather(lat, lon);
    }

    // 週間予報
    @GetMapping("/api/weather/weekly")
    public String getWeeklyWeather(@RequestParam double lat, @RequestParam double lon) {
        return weatherService.getWeeklyWeather(lat, lon);
    }
}