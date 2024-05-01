package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.controller.exeptions.StartEndDateException;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.service.WeatherService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequestMapping("/api/weather")
@RestController
public class WeatherRestController {

    private final WeatherService weatherService;
    private static final Logger logger = LoggerFactory.getLogger(WeatherRestController.class);
    public WeatherRestController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{unit}/{cityName}")
    public String getWeatherWithUnits(@PathVariable String unit, @PathVariable String cityName,
                                      @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
                                      @RequestParam @DateTimeFormat(pattern="HH:mm") LocalTime  startTime,
                                      @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate,
                                      @RequestParam @DateTimeFormat(pattern="HH:mm") LocalTime endTime) throws IOException, StartEndDateException {
            logger.info("Pomyślnie pobrano parametry");
          // stworzenie dat z pobranych parametrów w odpowiednim formacie, ew wyjątek jeśli data startowa później niż końcowa
           LocalDateTime start = LocalDateTime.of(startDate,startTime);
           LocalDateTime end = LocalDateTime.of(endDate,endTime);
           if(start.isAfter(end)){
               logger.error("Data startu później niż początku");
               throw new StartEndDateException();
           }
            return weatherService.getObjectWeatherInfo(cityName, unit, start,end); //metoda wypisująca pogodę dla wybranego zakresu czasowego i miasta

        }

}
