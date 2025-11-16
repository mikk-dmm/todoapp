package com.example.todoapp.controller.api;

import com.example.todoapp.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // 現在の天気
    @Operation(
        summary = "指定した位置情報（緯度・経度）の現在の天気情報を取得します。",
        description = "緯度(lat) と 経度(lon) をクエリパラメータとして受け取り、Open-Meteo API を呼び出してリアルタイムの天気データを返します。※ 現在地の取得（GPSなど）はクライアント側で行い、取得した座標を本APIに渡すことが想定されています。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取得成功"),
        @ApiResponse(responseCode = "400", description = "パラメータが不正です")
    })
    @GetMapping("/api/weather")
    public JsonNode getWeather(@Parameter(description = "緯度（例：35.68）")
                                @RequestParam double lat,
                                @Parameter(description = "経度（例：139.76）")
                                @RequestParam double lon) {
        return weatherService.getWeather(lat, lon);
    }

}
