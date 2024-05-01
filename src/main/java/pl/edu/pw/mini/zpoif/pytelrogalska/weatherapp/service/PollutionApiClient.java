package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.service;

import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.net.URL;
@Service
public final class PollutionApiClient {
    // metoda zwraca url, który będziemy podawać przy tworzeniu obiektu z PollutionApi
    public URL getPollutionJsonFormApi(String lat, String lng) throws MalformedURLException {
        String apiUrl = String.format("https://api.openweathermap.org/data/2.5/air_pollution/forecast?lat=%s&lon=%s&appid=%s",
                lat, lng, WeatherService.API_KEY);
        return new URL(apiUrl);
    }
}
