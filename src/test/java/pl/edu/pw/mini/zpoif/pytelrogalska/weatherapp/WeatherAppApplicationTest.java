package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherAppApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testIfIsUp() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/actuator/health";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);
        String expectedJsonResponse = "{\"status\":\"UP\"}";

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assert responseEntity.getBody() != null;
        assertEquals(expectedJsonResponse, responseEntity.getBody());

    }
    @Test
    void testEmptyInput() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw?startDate=&startTime=&endDate=&endTime=";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);
        String responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Błędne połączenie z serwerem. Sprawdż połączenie z internetem lub wpisz poprawną nazwę miasta", responseBody);
    }
    @Test
    void testEmptyInput2() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw?startDate=2024-01-22&startTime=&endDate=&endTime=";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);
        String responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Błędne połączenie z serwerem. Sprawdż połączenie z internetem lub wpisz poprawną nazwę miasta", responseBody);
    }
    @Test
    void testEmptyInput3() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw?startDate=2024-01-22&startTime=20:00&endDate=&endTime=";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);
        String responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Błędne połączenie z serwerem. Sprawdż połączenie z internetem lub wpisz poprawną nazwę miasta", responseBody);
    }

    @Test
    void testWrongInput() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw?startDate=2024-01-22&startTime=20:00&endDate=2024-01-22&endTime=21:00";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);
        String responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assert responseBody != null;
        assertEquals("<b>SELECTED CITY :  Warsaw</b><br>Niestety nie mamy danych dla podanego zakresu czasowego. Proszę spróbować podać inny zakres", responseBody);
    }


    @Test
    void testCorrectInput() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw?startDate=2024-01-24&startTime=15:00&endDate=2024-01-27&endTime=20:00";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);
        String responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assert responseBody != null;
        assertEquals("<b>SELECTED CITY :  Warsaw</b><br><br><i>Informacje pogodowe w wybranym zakresie czasowym:", responseBody.substring(0, 90));
    }

    @Test
    void testStatusCodeIs400() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw?startDate=2024-0112-22&startTime=20:00&endDate=2024-01-24&endTime=20:00";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testStatusCodeIs500() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }


    @Test
    void testPastDateInput() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw?startDate=2023-01-22&startTime=20:00&endDate=2023-01-24&endTime=20:00";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);
        String responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assert responseBody != null;
        assertEquals("<b>SELECTED CITY :  Warsaw</b><br>Niestety nie mamy danych dla podanego zakresu czasowego. Proszę spróbować podać inny zakres", responseBody);
    }

    @Test
    void testInvalidStartEndDate() {
        // Arrange
        String baseUrl = "http://localhost:" + port;
        String url = "/api/weather/metric/Warsaw?startDate=2023-01-25&startTime=20:00&endDate=2023-01-24&endTime=20:00";

        // Act
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + url, String.class);
        String responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Data startowa jest później niż data końcowa. Proszę o poprawienie dat.", responseBody);
    }
}