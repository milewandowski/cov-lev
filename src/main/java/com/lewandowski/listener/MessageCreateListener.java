package com.lewandowski.listener;

import com.lewandowski.entity.CountryResponse;
import com.lewandowski.service.CovidService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class MessageCreateListener implements EventListener<MessageCreateEvent> {

    private final CovidService covidService;
    private String response;

    public MessageCreateListener(CovidService covidService) {
        this.covidService = covidService;
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
                .filter(mess -> mess.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(mess -> checkIfIsCommandAndSetResponse(mess.getContent()))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(response))
                .then();
    }

    private boolean checkIfIsCommandAndSetResponse(String messageContent) {
        if(!messageContent.startsWith("!")) {
            return false;
        }

        if(messageContent.equalsIgnoreCase("!help")) {
            response = "Available commands:\n!help - get all available commands\n!statistics CountryName - get statistics for CountryName";
            return true;
        } else if(messageContent.startsWith("!statistics")) {
            createStatisticsResponse(messageContent);
            return true;
        }
        return false;
    }

    private void createStatisticsResponse(String messageContent) {
        String[] message = messageContent.split(" ");
        Optional<String> country = covidService.checkIfCountryNameExists(message[1]);

        if(country.isPresent()) {
            processCountryResponse(covidService.getStatisticsForCountry(country.get()).block().getResponse().get(0));
        } else {
            response = "Country unavailable :(";
        }
    }

    private void processCountryResponse(CountryResponse countryResponse) {
        response = new StringBuilder().append("COVID-19 statistics for: ")
                .append(countryResponse.getCountry())
                .append("\n")
                .append("New cases: ")
                .append(countryResponse.getCases().getNewCases())
                .append("\n")
                .append("Active cases: ")
                .append(countryResponse.getCases().getActive().toString())
                .append("\n")
                .append("New deaths: ")
                .append(countryResponse.getDeaths().getNewDeaths())
                .append("\n")
                .append("Total number of deaths: ")
                .append(countryResponse.getDeaths().getTotal().toString())
                .append("\n")
                .append("Total number of tests performed: ")
                .append(countryResponse.getTests().getTotal().toString())
                .append("\n")
                .append("Statistics date: ")
                .append(countryResponse.getDay())
                .toString();
    }

}
