package com.lewandowski.service;

import com.lewandowski.entity.CountriesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Service
@PropertySource("classpath:APIKeys.properties")
public class CovidService {

    @Value("${x.rapidapi.key}")
    private String apiKey;
    private final WebClient.Builder webClientBuilder;
    private CountriesResponse countriesResponse;

    public CovidService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public CountriesResponse getAllCountries() {
        if(countriesResponse == null) {
            countriesResponse = webClientBuilder
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader("x-rapidapi-key", apiKey)
                    .build()
                    .get()
                    .uri("https://covid-193.p.rapidapi.com/countries")
                    .retrieve()
                    .bodyToMono(CountriesResponse.class)
                    .block();
        }
        return countriesResponse;
    }

}
