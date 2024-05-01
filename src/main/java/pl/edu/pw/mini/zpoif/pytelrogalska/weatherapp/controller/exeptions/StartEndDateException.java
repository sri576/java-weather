package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.controller.exeptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StartEndDateException extends Exception{ //własny błąd dla daty startowej późniejszej niż początkowa
    public StartEndDateException() {
    }
}
