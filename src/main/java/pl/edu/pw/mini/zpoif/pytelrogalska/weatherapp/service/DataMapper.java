package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.model.MainObject;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.pollution.MainPollutionObject;

import java.io.IOException;
import java.net.URL;
@Service
public final class DataMapper {
    private final WeatherApiClient weatherApiClient;
    private final PollutionApiClient pollutionApiClient;
    private final JsonReader jsonReader;

    public DataMapper(WeatherApiClient weatherApiClient, PollutionApiClient pollutionApiClient, JsonReader jsonReader) {
        this.weatherApiClient = weatherApiClient;
        this.pollutionApiClient = pollutionApiClient;
        this.jsonReader = jsonReader;
    }
    //metoda tworząca obiekt z hierarchii klas dla API pogodowego dla wybranego miasta
    public MainObject generateObjectFromJson(String cityName, String units ) throws IOException {
        URL url = weatherApiClient.getWeatherJsonFormApi(cityName,units);
        String jsonString = jsonReader.readJsonString(url);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, MainObject.class);
    }
    // metoda tworząca obiekt z hierarchii klas dla API o zanieczyszczeniach powietrza dla podanych współrzędnych
    public MainPollutionObject generatePollutionFromJSON(String lat, String lng) throws IOException {
        URL url = pollutionApiClient.getPollutionJsonFormApi(lat,lng);
        String jsonString = jsonReader.readJsonString(url);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, MainPollutionObject.class);
    }
}
