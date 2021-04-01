package com.lewandowski.service;

import com.lewandowski.entity.CountriesResponse;
import com.lewandowski.entity.StatisticsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:APIKeys.properties")
public class CovidService {

    @Value("${x.rapidapi.key}")
    private String apiKey;
    private final WebClient.Builder webClientBuilder;
    private final String BASE_URI = "https://covid-193.p.rapidapi.com";
    private final List<String> countries = new ArrayList<>();

    public CovidService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void getAllCountries() {
        webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-rapidapi-key", apiKey)
                .build()
                .get()
                .uri(BASE_URI + "/countries")
                .retrieve()
                .bodyToMono(CountriesResponse.class)
                .subscribe(countriesResponse -> countries.addAll(countriesResponse.getResponse()));
    }

    public void getStatisticsForCountry(String countryName) {
        boolean isPresent = countries.stream()
                .anyMatch(country -> country.equals(countryName));
        if (isPresent) {
            Mono<StatisticsResponse> statisticsResponse = webClientBuilder
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader("x-rapidapi-key", apiKey)
                    .build()
                    .get()
                    .uri(BASE_URI + "/statistics?country=" + countryName)
                    .retrieve()
                    .bodyToMono(StatisticsResponse.class);
        }
    }

}
