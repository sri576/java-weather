package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URL;
@Service
public final class WeatherApiClient {
    // metoda zwraca url, który będziemy podawać przy tworzeniu obiektu z WeatherApi
    public URL getWeatherJsonFormApi(String cityName, String units) throws IOException {
        String apiUrl = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=%s",
                cityName, WeatherService.API_KEY, units);
        return new URL(apiUrl);
    }
}
