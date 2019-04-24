/**
 * 
 */
package com.example.wheater.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.example.wheater.dto.CityWeather;

/**
 * @author Arturo
 *
 */
@Controller
public class MyController {
	
	private final String APPID="ea8ec796bb2d2c59825ae8e4f3cf5331";
	
	/**
	 * 
	 * @param cityWeather
	 * @return
	 */
    @GetMapping("/getWeatherByCityId")
    public String sendForm(CityWeather cityWeather) {
        return "getWeatherByCityId";
    }
    
    /**
     * 
     * @param cityWeather
     * @return
     */
    @PostMapping("/getWeatherByCityId")
    public String processForm(CityWeather cityWeather) {    	
    	cityWeather.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
    	
    	RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> call= restTemplate.getForEntity("http://api.openweathermap.org/data/2.5/weather?id="+cityWeather.getCityId()+"&APPID="+APPID, String.class);
//        System.out.println(call.getBody());
        
        JSONObject jsonObject = (JSONObject)JSONValue.parse(call.getBody());
        JSONArray jsonArr = (JSONArray)jsonObject.get("weather");
        @SuppressWarnings("unchecked")
		Iterator<JSONObject> iterator = jsonArr.iterator();
        if (iterator.hasNext()) {
        	JSONObject jsonObjectMain = iterator.next();
        	cityWeather.setWeatherDescription(jsonObjectMain.get("description").toString());
        }
        JSONObject jsonObjectTemp = (JSONObject)jsonObject.get("main");
        Double temp = (Double)jsonObjectTemp.get("temp");
        Double celsius = temp-273.15;
        Double farenheit = celsius * (9/5) + 32;
        cityWeather.setTemperatureF(String.format("%.2f", farenheit));
        cityWeather.setTemperatureC(String.format("%.2f", celsius));
        
        JSONObject jsonObjectSys = (JSONObject)jsonObject.get("sys");
        long sunrise = (long)jsonObjectSys.get("sunrise");
        long sunset = (long)jsonObjectSys.get("sunset");
        Date sunriseDate = new Date(sunrise*1000L);
        Date sunsetDate = new Date(sunset*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        cityWeather.setSunriseTime(sdf.format(sunriseDate));
        cityWeather.setSunsetTime(sdf.format(sunsetDate));
        
        cityWeather.setCityName(jsonObject.get("name").toString());        

        return "showResult";
    }
    
}
