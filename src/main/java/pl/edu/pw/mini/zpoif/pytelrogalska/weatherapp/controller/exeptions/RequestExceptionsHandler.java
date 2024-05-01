package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.controller.exeptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class RequestExceptionsHandler {
    private static final Logger EXCEPTIONS_LOGGER = LoggerFactory.getLogger(RequestExceptionsHandler.class);
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)// błędy wyrzucane np przy podaniu daty,godziny w złym formacie lub daty,godziny która nie istnieje
    @ResponseStatus(HttpStatus.BAD_REQUEST) // kod błędu
    public ResponseEntity<String> handleInvalidDateFormatException(MethodArgumentTypeMismatchException ex) {
        EXCEPTIONS_LOGGER.error("BAD_REQUEST: wrong date/time parameter");
        return ResponseEntity.badRequest().body("Nieprawidłowy format daty/godziny lub nieistniejąca data."+"<br>"+ "Proszę podać daty w formacie yyyy-MM-dd oraz godziny w formacie HH:mm oraz upewnić się, że data oraz godzina są poprawne");
}

    @ExceptionHandler(Exception.class) //błędy wyrzucane przy podaniu Miasta, które nie istnieje. Nie ma o nim danych w API
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // kod błędu
    public ResponseEntity<String> handleInvalidParameter(Exception ex){
        EXCEPTIONS_LOGGER.error("INTERNAL_SERVER_ERROR: No such city");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błędne połączenie z serwerem. Sprawdż połączenie z internetem lub wpisz poprawną nazwę miasta");
    }
    @ExceptionHandler(StartEndDateException.class) // błędy wyrzucane przy podaniu daty startowej późniejszej niż początkowa
    @ResponseStatus(HttpStatus.BAD_REQUEST)// kod błędu
    public ResponseEntity<String> handleStartEndDateException(StartEndDateException e){
        EXCEPTIONS_LOGGER.error("BAD_REQUEST: End date before start date");
        return ResponseEntity.badRequest().body("Data startowa jest później niż data końcowa. Proszę o poprawienie dat.");
    }



}
