package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.config;

import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
@Configuration
public class Config {

    // Klasa pomocnicza do generowania hierarchii klas z przykładowo pobranego JSONA 1
    public void generateMainWeatherClasses(String cityName, String apikey) throws IOException {
        String apiUrl = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s",
                cityName, apikey);
        URL url = new URL(apiUrl);
        convertJsonToJavaClass(url,new File("C:\\Users\\micha\\Documents\\github\\zpoif_2023_projekt_pytelm_rogalskak\\WeatherApp\\src\\main\\java\\pl\\edu\\pw\\mini\\zpoif\\pytelrogalska\\weatherapp"),"model","MainObject");
    }
    // Klasa pomocnicza do generpwania hierarchii klas z pobranego JSONA 2
    public void generatePollutionClasses(Double lat, Double lng, String apikey) throws IOException{
        String apiUrl = String.format("http://api.openweathermap.org/data/2.5/air_pollution/forecast?lat=%s&lon=%s&appid=%s",
                lat,lng,apikey);
        URL url = new URL(apiUrl);
        convertJsonToJavaClass(url, new File("C:\\Users\\rogal\\IntelliJ-workspace\\Projekt\\WeatherApp\\src\\main\\java\\pl\\edu\\pw\\mini\\zpoif\\pytelrogalska\\weatherapp"),"pollution","MainPollutionObject");
    }

    // metoda tworząca hierarchię klas w podanym miejscu z formatu JSON
    private static void convertJsonToJavaClass(URL inputJsonUrl, File outputJavaClassDirectory, String packageName, String javaClassName)
            throws IOException {
        JCodeModel jcodeModel = new JCodeModel();
        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() {
                return true;
            }

            @Override
            public SourceType getSourceType() {
                return SourceType.JSON;
            } //określenie, że tworzymy klasy z formatu JSON
        };
        //określenie zasad na tworzenie klas, dodanie adnotacji,budowanie klas
        SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
        mapper.generate(jcodeModel, javaClassName, packageName, inputJsonUrl);
        jcodeModel.build(outputJavaClassDirectory);
    }
}
