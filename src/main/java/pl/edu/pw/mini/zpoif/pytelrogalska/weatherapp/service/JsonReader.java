package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
@Service
public final class JsonReader {

    private static final Logger READER_LOGGER = LoggerFactory.getLogger(JsonReader.class);

    // metoda zwraca odczytanego Jsona w formie String z podanego url
    public String readJsonString(@NotNull URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // metoda GET - chcemy dostać dane z podanego url
        String jsonString = null;

        int responseCode = connection.getResponseCode(); //pobieramy reponse code
        if (responseCode == HttpURLConnection.HTTP_OK) { //200
            READER_LOGGER.info("Http connection successful with code:" + responseCode +" Reading Json from url");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            //budujemy string ze wczytywanych danych
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            jsonString = response.toString();
        }
        else { //jeśli response code nie ok
            READER_LOGGER.error("Http request failed with code: "+ responseCode +" unable to read Json from url");
        }
        return jsonString;
    }
    //metoda zwraca response code z polaczenia
    public int getResponseCode1(@NotNull URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection.getResponseCode();
    }
}
