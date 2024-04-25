package com.mediatica.onlinebanking.currencyAPI;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class APIRequest {

    public BigDecimal getExchangeRate(String originalCurrency, String conversionCurrency)
    {
        try {
            // Formatting the API endpoint URL, such that the exchange course is retrieved for the specified currency parameter, called 'originalCurrency'
            String urlStr = String.format("https://open.er-api.com/v6/latest/%s", originalCurrency);

            // Making Request
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Reading Response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // Parsing JSON and getting the currency rates list as 'Set'
            String responseData = response.toString();
            JsonObject jobj = new Gson().fromJson(responseData, JsonObject.class);
            JsonObject rates = jobj.get("rates").getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = rates.entrySet(); //will return the list of all currency rates


            for (Map.Entry<String, JsonElement> entry : entries)
                if (entry.getKey().equals(conversionCurrency))
                    return entry.getValue().getAsBigDecimal(); //Conversion rate number is returned

        }

        catch (IOException e) {
            e.printStackTrace();

        }

        return null;
    }

}