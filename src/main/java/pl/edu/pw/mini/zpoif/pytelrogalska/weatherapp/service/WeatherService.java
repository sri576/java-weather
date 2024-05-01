package pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.config.Config;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.model.List;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.model.Main;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.model.MainObject;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.pollution.MainPollutionObject;
import pl.edu.pw.mini.zpoif.pytelrogalska.weatherapp.pollution.PollutionList;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

@Service
public class WeatherService {

    private final Config config;
    protected final WeatherApiClient weatherApiClient;
    static final String API_KEY = "13662892eb587f841013ffacb523c53e";
    private final DataMapper dataMapper;
    private static final Logger SERVICE_LOGGER = LoggerFactory.getLogger(WeatherService.class);

    @Autowired
    public WeatherService(Config config, WeatherApiClient weatherApiClient, DataMapper dataMapper) {
        this.config = config;
        this.weatherApiClient = weatherApiClient;
        this.dataMapper = dataMapper;
    }

    @PostConstruct //najpierw wywołana metoda tylko generateMainWeatherClasses,
    // żeby potem móc działać na klasach i obiektach, potem generowanie PollutionClasses
    public void init() throws IOException {
       /* String cityName = "Warsaw";
        config.generateMainWeatherClasses(cityName,apikey);
        MainObject objectExample = generateObjectFromJson("Warsaw","metric");
        Double lat = objectExample.getCity().getCoord().getLat();
        Double lng = objectExample.getCity().getCoord().getLon();
        config.generatePollutionClasses(lat,lng,apikey);*/
    }

    // metoda generuje informacje pogodowe oraz o zanieczyszczeniu powietrza dla danego miasta w wybranym zakresie czasowym
    public String getObjectWeatherInfo(String cityName, String units, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        MainObject weatherObject = dataMapper.generateObjectFromJson(cityName,units); //obiekt z WeatherApi
        SERVICE_LOGGER.info("Main Weather Object created with city: "+ cityName);
        String lat = String.valueOf(weatherObject.getCity().getCoord().getLat()); //pobranie wspólrzędnych podanego miasta
        String lng = String.valueOf(weatherObject.getCity().getCoord().getLon());
        MainPollutionObject pollutionObject = dataMapper.generatePollutionFromJSON(lat,lng);// obiekt z PollutionApi dla wybranego miasta
        SERVICE_LOGGER.info("Main Pollution Object created with lattitude:"+ lat);
        //pobranie list z elementami z obu obiektów
        java.util.List<List> lista = weatherObject.getList();
        java.util.List<PollutionList> listaPollution = pollutionObject.getList();


        StringBuilder infoPogodowe = new StringBuilder();
        // sprawdzenie czy podane jednostki są prawidłowe, jeśli nie to API default i informacja na ekranie
        if(!(units.equals("metric") || units.equals("standard") || units.equals("imperial"))){
            SERVICE_LOGGER.info("Incorrect units. Weather shown in default units");
            infoPogodowe.append("<i>"+"Uwaga : Podane jednostki temperatury są w nieprawidłowym formacie. Domyślne jednostki to: Kelvin, m/s."+"<br>"+ "Aby zobaczyć temperaturę w Celsjuszach użyj : metric, w Farenheitach użyj : imperial."+"<br>"+"<br>"+"</i>");
        }
        // jeśli prawidłowe to ustawiamy
        String jednostkaT;
        jednostkaT = switch (units) {
            case "metric" -> " °C ";
            case "imperial" -> " °F ";
            default -> " K ";
        };

        String jednostkaV;
        jednostkaV = switch (units){
            case "imperial" -> "miles/h";
            default -> "m/s";
        };

        boolean czySaDane=false; //flaga czy znajdziemy dane w wybranym zakresie czasowym
        infoPogodowe.append("<i>"+"Informacje pogodowe w wybranym zakresie czasowym:"+"<br>"+"<br>"+"</i>");
        java.util.List<LocalDateTime> listaDat = new ArrayList<>(); //tworzymy liste na daty, które wystąpią w WeatherAPI
        for(List list: lista){
            // zamiana pobranej daty na format LocalDateTime
            Integer wDate = list.getDt();
            Date wDateFixed = new Date(wDate *1000L);
            LocalDateTime wlocalDate = wDateFixed.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            //jeśli data w podanym przedziale to pobieramy pogodę
            if(wlocalDate.isAfter(startDate) && wlocalDate.isBefore(endDate)){
                czySaDane=true; //zmiana flagi
                listaDat.add(wlocalDate); // dodanie daty do listy dat i pobranie informacji
                String data = ""+wlocalDate;
                infoPogodowe.append("<b>" + "POGODA W DNIU:  ").append(data, 0, 10).append(" O GODZINIE:  ").append(data, 11, 16).append("</b>");
                infoPogodowe.append("<br>");
                Main main = list.getMain();
                infoPogodowe.append("<b>" + "<i>" + "  Temperatura: " + "</b>" + "</i>" + "<br>" + " Rzeczywista: ").append(main.getTemp()).append(" ").append(jednostkaT).append("<br>");
                infoPogodowe.append("<i>" + " Odczuwalna: " + "</i>").append(main.getFeelsLike()).append(" ").append(jednostkaT).append("<br>");
                infoPogodowe.append("<b>" + "  Zachmurzenie : " + "<br>" + "</b>").append(list.getClouds().getAll()).append("%").append("<br>");
                infoPogodowe.append("<b>" + " Widoczność: " + "<br>" + "</b>").append(list.getVisibility()).append("m").append("<br>");
                infoPogodowe.append("<b>" + " Wiatr  : " + "<br>" + "</b>" + "<i>" + " Prędkość: " + "</i>").append(list.getWind().getSpeed()).append(" ").append(jednostkaV);
                infoPogodowe.append("<br>" + "<i>" + "Kierunek: " + "</i>").append(list.getWind().getDeg()).append("<br>").append("<i>").append(" Porywy: ").append("</i>").append(list.getWind().getGust()).append(" ").append(jednostkaV);
                infoPogodowe.append("<br>" + "<b>" + "Wilgotność: " + "</b>" + "<br>").append(list.getMain().getHumidity()).append("%");
                // przejscie do danych z PollutionAPI
                infoPogodowe.append("<b>"+"<br>"+"Jakość powietrza"+"</b>"+"<br>");
                for(PollutionList pl : listaPollution){
                    // zamiana daty na LocalDateTime
                    Integer pDate= pl.getDt();
                    Date pDateFixed = new Date(pDate *1000L);
                    LocalDateTime pDateLocal = pDateFixed.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    // sprawdzamy, czy data jest zgodna z jakąś z dat dla informacji pogodowych i pobranie danych
                    if(listaDat.contains(pDateLocal)){
                        infoPogodowe.append("<i>" + "Index jakości  : " + "</i>").append(+pl.getMain().getAqi());
                        Integer index = pl.getMain().getAqi();
                        switch (index){ // wyjaśnienie znaczenia indeksów aqi
                            case 1:
                                infoPogodowe.append(" (jakość dobra)");
                                break;
                            case 2:
                                infoPogodowe.append(" (jakość w porządku)");
                                break;
                            case 3:
                                infoPogodowe.append(" (jakość średnia)");
                                break;
                            case 4:
                                infoPogodowe.append(" (jakość słaba)");
                                break;
                            case 5:
                                infoPogodowe.append(" (jakość bardzo słaba)");
                        }
                        infoPogodowe.append("<br>" + "<i>" + "Stężenie tlenku węgla (CO) : " + "</i>").append(pl.getComponents().getCo()).append("  µg/m³");
                        infoPogodowe.append("<br>" + "<i>" + "Stężenie tlenku azotu (NO): " + "</i>").append(pl.getComponents().getNo()).append(" µg/m³");
                        infoPogodowe.append("<br>" + "<i>" + "Stężenie dwutlenku azotu (NO²): " + "</i>").append(pl.getComponents().getNo2()).append(" µg/m³");
                        infoPogodowe.append("<br>" + "<i>" + "Stężenie ozonu (O³): " + "</i>").append(pl.getComponents().getO3()).append(" µg/m³");
                        infoPogodowe.append("<br>" + "<i>" + "Stężenie dwutlenku siarki (SO²): " + "</i>").append(pl.getComponents().getSo2()).append(" µg/m³");
                        infoPogodowe.append("<br>" + "<i>" + "Stężenie pyłków zawieszonych: " + "</i>").append(pl.getComponents().getPm25()).append(" µg/m³");
                        infoPogodowe.append("<br>" + "<i>" + "Stężenie amoniaku (NH³): " + "</i>").append(pl.getComponents().getNh3()).append(" µg/m³");
                        // usunięcie danej daty z listy dat. Jeśli nie usuniemy to w każdej kolejnej informacji będą też dane z poprzedniej daty
                        Iterator<LocalDateTime> iterator = listaDat.iterator();
                        while (iterator.hasNext()) {
                            LocalDateTime current = iterator.next();
                            if (current.equals(pDateLocal)) {
                                iterator.remove();
                            }
                        }
                    }
                }
                infoPogodowe.append( "<br>"+"<br>");

            }}

        // sprawdzamy flagę czy znaleziono dane dla danego przedziału, jeśli nie to komunikat, jeśli tak to informacje pogodowe
        if(!czySaDane){
            SERVICE_LOGGER.info("No weather data between given dates");
            return "<b>"+"SELECTED CITY :  " + cityName+"</b>" + "<br>"+ "Niestety nie mamy danych dla podanego zakresu czasowego. Proszę spróbować podać inny zakres";
        }
        else{
            SERVICE_LOGGER.info("Weather data shown");
        }
        return "<b>"+"SELECTED CITY :  " + cityName+"</b>" + "<br>"+ "<br>" +infoPogodowe + "<br>"+"<br>"+ "Informacje specjalistyczne:"+"<br>" ;
    }


}
