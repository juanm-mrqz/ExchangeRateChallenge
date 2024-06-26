package com.alura.app.servicios;

import com.alura.app.currency.CurrencyData;
import com.alura.app.currency.QuotaData;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiAccess {

    public String callApi(String url) {
        try {
            //Metodo GET
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.out.println("Error de consulta: " + e.getMessage());
        }
        return null;
    }

    public CurrencyData getCurrencyResponse(String url) {
        return new GsonBuilder().create().fromJson(callApi(url), CurrencyData.class);
    }

    public QuotaData getQuotaResponse(String url) {
        return new GsonBuilder().create().fromJson(callApi(url), QuotaData.class);
    }
}
