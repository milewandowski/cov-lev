package com.lewandowski.listener;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageCreateListener implements EventListener<MessageCreateEvent> {

    private String response;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
                .filter(mess -> mess.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(mess -> isCommand(mess.getContent()))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(response))
                .then();
    }

    private boolean isCommand(String content) {
        boolean command = false;
        if(content.equals("!help")) {
            response = "Available commands:\n!help\n!stat";
            command = true;
        } else if(content.equals("!stat")) {
            response = "Todo";
            command = true;
        }
        return command;
    }

}
