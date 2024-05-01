package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.controller.exeptions.StartEndDateException;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.service.WeatherService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class WeatherRestControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherRestController weatherRestController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = standaloneSetup(weatherRestController).build();
    }

    @Test
    public void testGetWeatherWithUnits() throws Exception {
        // Mocking the behavior of WeatherService
        when(weatherService.getObjectWeatherInfo(any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn("Mocked response");

        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDate endDate = LocalDate.of(2024, 1, 2);
        LocalTime endTime = LocalTime.of(12, 0);

        ResultActions resultActions = mockMvc.perform(get("/api/weather/metric/Warsaw")
                        .param("startDate", startDate.toString())
                        .param("startTime", startTime.toString())
                        .param("endDate", endDate.toString())
                        .param("endTime", endTime.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String content = result.getResponse().getContentAsString();

        // Add assertions based on the expected behavior
        assertEquals("Mocked response", content);
    }
    @Test
    public void testIncorrectDate() throws Exception {


        // Define the behavior of the mocked service
        when(weatherService.getObjectWeatherInfo(any(), any(), any(), any()))
                .thenReturn("Mocked response");

        // Perform a request with incorrect date
        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/units/city")
                        .param("startDate", "2024--24")
                        .param("startTime", "1200")
                        .param("endDate", "25-1002-25")
                        .param("endTime", "18:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
    @Test
    public void testCorrectDate() throws Exception {


        // Define the behavior of the mocked service
        when(weatherService.getObjectWeatherInfo(any(), any(), any(), any()))
                .thenReturn("Mocked response");

        // Perform a request with incorrect date
        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/units/city")
                        .param("startDate", "2024-01-24")
                        .param("startTime", "12:00")
                        .param("endDate", "2024-01-25")
                        .param("endTime", "18:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Mocked response"));
    }
    @Test
    void testGetWeatherWithWrongDateOrder() throws IOException {
        // Create a LocalDateTime with an end date earlier than the start date
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 25, 12, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 24, 12, 0);

        // Set up the mock to throw an IOException for this specific case
        when(weatherService.getObjectWeatherInfo(any(), any(), eq(startDate), eq(endDate)))
                .thenThrow(new IOException("End date cannot be earlier than start date"));

        // Perform the test
        assertThrows(StartEndDateException.class, () -> {
            weatherRestController.getWeatherWithUnits("unit", "city", startDate.toLocalDate(),
                    startDate.toLocalTime(), endDate.toLocalDate(), endDate.toLocalTime());
        });
    }

    @Test
    void testGetWeatherWithPastEndDate() throws IOException {
        // Create a LocalDateTime with an end date earlier than the start date
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 23, 12, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 25, 12, 0);

        // Set up the mock to throw an IOException for this specific case
        when(weatherService.getObjectWeatherInfo(any(), any(), eq(startDate), eq(endDate)))
                .thenThrow(new IOException("Podana zostałą data z przeszłości"));

        // Perform the test
        assertThrows(IOException.class, () -> {
            weatherRestController.getWeatherWithUnits("unit", "city", startDate.toLocalDate(),
                    startDate.toLocalTime(), endDate.toLocalDate(), endDate.toLocalTime());
        });
    }
}
