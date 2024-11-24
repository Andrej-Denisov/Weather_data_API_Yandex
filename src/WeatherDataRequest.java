import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class WeatherDataRequest {

    public static void weatherData() throws IOException {

        String apiKey = "1b310f0a-d5a8-4a72-9723-6dd247800c7e";
        String apiUrl = "https://api.weather.yandex.ru/v2/forecast?lat=57.9194&lon=59.965&lang=ru_RU&limit=1&hours=true&extra=true";
        double tempMax = 0;
        double tempMin = 0;
        double averageTemperature = 0;

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrl);
        httpGet.addHeader("X-Yandex-Weather-Key", apiKey);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                System.err.println("Ошибка HTTP: " + statusCode + " " + response.getStatusLine().getReasonPhrase());
                return;
            }

            HttpEntity entity = response.getEntity();
            String jsonString = EntityUtils.toString(entity);

            System.out.println(jsonString);
            System.out.println("Основные данные параметров погоды (с расшифровкой и расчетом средней температуры).");

            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("now_dt")) {
                String nowDt = jsonObject.getString("now_dt");
                System.out.println("Дата и время: " + nowDt);
            } else {
                System.err.println("Ошибка: данные отсутствуют.");
            }
            if (jsonObject.has("fact")) {
                JSONObject fact = jsonObject.getJSONObject("fact");
                if (fact.has("temp")) {
                    double temperature = fact.getDouble("temp");
                    System.out.println("Текущая температура (град): " + temperature);
                } else {
                    System.err.println("Ошибка: данные отсутствуют.");
                }
                if (fact.has("condition")) {
                    String condition = fact.getString("condition");
                    System.out.print("Погодные условия: ");
                    switch (condition) {
                        case "clear":
                            System.out.print("ясно");
                            break;
                        case "partly-cloudy":
                            System.out.print("малооблачно");
                            break;
                        case "cloudy":
                            System.out.print("облачно с прояснениями");
                            break;
                        case "overcast":
                            System.out.print("пасмурно");
                            break;
                        case "light-rain":
                            System.out.print("небольшой дождь");
                            break;
                        case "rain":
                            System.out.print("дождь");
                            break;
                        case "heavy-rain":
                            System.out.print("сильный дождь");
                            break;
                        case "showers":
                            System.out.print("ливень");
                            break;
                        case "wet-snow":
                            System.out.print("дождь со снегом");
                            break;
                        case "light-snow":
                            System.out.print("небольшой снег");
                            break;
                        case "snow":
                            System.out.print("снег");
                            break;
                        case "snow-showers":
                            System.out.print("снегопад");
                            break;
                        case "hail":
                            System.out.print("град");
                            break;
                        case "thunderstorm":
                            System.out.print("гроза");
                            break;
                        case "thunderstorm-with-rain":
                            System.out.print("дождь с грозой");
                            break;
                        case "thunderstorm-with-hail":
                            System.out.print("гроза с градом");
                            break;
                        default:
                            System.out.print("Ошибка данных");
                    }
                    System.out.println();
                }
                if(fact.has("feels_like")){
                    double feelsLike = fact.getDouble("feels_like");
                    System.out.println("Ощущается как (град): " + feelsLike);
                }
                if(fact.has("wind_speed")){
                    double windSpeed = fact.getDouble("wind_speed");
                    System.out.println("Скорость ветра (м/с): " + windSpeed);
                }
                if(fact.has("pressure_mm")){
                    double pressureMm = fact.getDouble("pressure_mm");
                    System.out.println("Давление (мм рт.ст.): " + pressureMm);
                }
                if(fact.has("humidity")){
                    double humidity = fact.getDouble("humidity");
                    System.out.println("Влажность (%): " + humidity);
                }
            } else {
                System.err.println("Ошибка: данные отсутствуют.");
            }
            if (jsonObject.has("forecasts")) {
                JSONArray forecastsArray = jsonObject.getJSONArray("forecasts");
                for (int i = 0; i < forecastsArray.length(); i++) {
                    JSONObject forecast = forecastsArray.getJSONObject(i);
                    if (forecast.has("date")) {
                        String date = forecast.getString("date");
                        if(forecast.has("parts")){
                            JSONObject parts = forecast.getJSONObject("parts");
                            if(parts.has("day") && parts.getJSONObject("day").has("temp_max")){
                                tempMax = parts.getJSONObject("day").getDouble("temp_max");
                            }
                            if(parts.has("day") && parts.getJSONObject("day").has("temp_min")){
                                tempMin = parts.getJSONObject("day").getDouble("temp_min");
                            }
                        }
                    }
                }
            } else {
                System.err.println("Ошибка: данные отсутствуют.");
            }
            averageTemperature = (tempMax + tempMin) / 2;
            System.out.println("Средняя температура (расчетная): " + averageTemperature);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}

