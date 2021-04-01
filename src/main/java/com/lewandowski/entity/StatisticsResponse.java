package com.lewandowski.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatisticsResponse {

    private Integer results;
    private List<CountryResponse> response;

    public Integer getResults() {
        return results;
    }

    public void setResults(Integer results) {
        this.results = results;
    }

    public List<CountryResponse> getResponse() {
        return response;
    }

    public void setResponse(List<CountryResponse> response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "StatisticsResponse{" +
                "results=" + results +
                ", countriesResponsesList=" + response +
                '}';
    }
}
