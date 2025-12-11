package org.example.geolocation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Cliente para validação de endereços usando OpenStreetMap Nominatim API.
 *
 * Usa a API gratuita do OpenStreetMap para verificar se uma rua existe
 * no bairro de Ipiranga, São Paulo.
 *
 * **Importante**: A API tem limite de requisições. Use com moderação.
 * Para produção, considere fazer cache dos resultados.
 *
 * @version 1.0
 * @author Property Management System
 */
@Slf4j
public class GeolocationValidator {

    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search";
    private static final String USER_AGENT = "PropertyManagementSystem/1.0";
    private static final int REQUEST_DELAY_MS = 1000; // Respeitar rate limit da API
    private static final Gson gson = new Gson();

    private long lastRequestTime = 0;

    /**
     * Valida se uma rua existe em Ipiranga, São Paulo.
     *
     * @param street Nome da rua (ex: "Rua Vicente da Costa")
     * @return true se a rua foi encontrada, false caso contrário
     */
    public boolean validateStreet(String street) {
        try {
            // Respeitar rate limit (1 requisição por segundo)
            respectRateLimit();

            String query = String.format("%s, Ipiranga, São Paulo, Brazil", street);
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s&format=json&limit=1", NOMINATIM_API_URL, encodedQuery);

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("User-Agent", USER_AGENT);

                ClassicHttpResponse response = httpClient.execute(httpGet);

                if (response.getCode() == 200) {
                    String responseBody = new String(response.getEntity().getContent().readAllBytes());
                    JsonArray results = gson.fromJson(responseBody, JsonArray.class);

                    boolean found = results != null && results.size() > 0;

                    if (found) {
                        JsonObject result = results.get(0).getAsJsonObject();
                        System.out.println("✓ Rua validada: " + street);
                    } else {
                        System.out.println("✗ Rua não encontrada: " + street);
                    }

                    return found;
                } else {
                    System.err.println("Erro na API: HTTP " + response.getCode());
                    return false;
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao validar rua: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtém informações de localização de uma rua.
     *
     * @param street Nome da rua
     * @return Objeto com latitude, longitude e nome completo
     */
    public LocationInfo getLocationInfo(String street) {
        try {
            respectRateLimit();

            String query = String.format("%s, Ipiranga, São Paulo, Brazil", street);
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s&format=json&limit=1", NOMINATIM_API_URL, encodedQuery);

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("User-Agent", USER_AGENT);

                ClassicHttpResponse response = httpClient.execute(httpGet);

                if (response.getCode() == 200) {
                    String responseBody = new String(response.getEntity().getContent().readAllBytes());
                    JsonArray results = gson.fromJson(responseBody, JsonArray.class);

                    if (results != null && results.size() > 0) {
                        JsonObject result = results.get(0).getAsJsonObject();
                        return LocationInfo.builder()
                                .street(street)
                                .latitude(result.get("lat").getAsDouble())
                                .longitude(result.get("lon").getAsDouble())
                                .displayName(result.get("display_name").getAsString())
                                .found(true)
                                .build();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao obter informações de localização: " + e.getMessage());
        }

        return LocationInfo.builder()
                .street(street)
                .found(false)
                .build();
    }

    /**
     * Respeita o rate limit da API (1 requisição por segundo).
     */
    private void respectRateLimit() {
        long now = System.currentTimeMillis();
        long timeSinceLastRequest = now - lastRequestTime;

        if (timeSinceLastRequest < REQUEST_DELAY_MS) {
            try {
                Thread.sleep(REQUEST_DELAY_MS - timeSinceLastRequest);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        lastRequestTime = System.currentTimeMillis();
    }

    /**
     * DTO com informações de localização.
     */
    @lombok.Data
    @lombok.Builder
    public static class LocationInfo {
        private String street;
        private Double latitude;
        private Double longitude;
        private String displayName;
        private boolean found;
    }
}
