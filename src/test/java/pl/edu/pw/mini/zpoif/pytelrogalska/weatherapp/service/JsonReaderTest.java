package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;


@ExtendWith(MockitoExtension.class)
class JsonReaderTest {

    @Mock
    private HttpURLConnection httpURLConnection;

    @InjectMocks
    private JsonReader jsonReader;

    @Test
    void testReadJsonString404() throws IOException {
        // Arrange
        URL testUrl = new URL("https://api.openweathermap.org/data/2.5/forecast?q=Warsaws&appid=13662892eb587f841013ffacb523c53e&units=metric");
        lenient().when(httpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        // Act
        int responseCode = jsonReader.getResponseCode1(testUrl);

        // Assert
        assertEquals(404, responseCode);
    }
    @Test
    void testReadJsonString200() throws IOException {
        // Arrange
        URL testUrl = new URL("https://api.openweathermap.org/data/2.5/forecast?q=Warsaw&appid=13662892eb587f841013ffacb523c53e&units=metric");
        lenient().when(httpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        // Act
        int responseCode = jsonReader.getResponseCode1(testUrl);

        // Assert
        assertEquals(200, responseCode);
    }
    @Test
    void testReadJsonStringNotNULL() throws IOException {
        // Arrange
        URL testUrl = new URL("https://api.openweathermap.org/data/2.5/forecast?q=Warsaw&appid=13662892eb587f841013ffacb523c53e&units=metric");
        lenient().when(httpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Act
        String result = jsonReader.readJsonString(testUrl);

        // Assert
        assertNotNull(result);
    }
    @Test
    void testReadJsonStringNULL() throws IOException {
        // Arrange
        URL testUrl = new URL("https://api.openweathermap.org/data/2.5/forecast?q=Warsaw&appid=13662892eb587f84");
        lenient().when(httpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        // Act
        String result = jsonReader.readJsonString(testUrl);

        // Assert
        assertNull(result);
    }
    @Test
    void testReadJsonStringIOException() throws IOException {
        // Arrange
        URL testUrl = new URL("https://api.openweathermap.org/data/2.5/forecast?q=Warsaw&appid=13662892eb587f84");
        lenient().when(httpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        // Act
        String result = jsonReader.readJsonString(testUrl);

        // Assert
        assertNull(result);
    }

}