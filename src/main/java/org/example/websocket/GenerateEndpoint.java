package org.example.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.log4j.Log4j2;
import org.example.model.Message;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Objects.isNull;

@Log4j2
@ServerEndpoint(value = "/generate/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class GenerateEndpoint {
    private Session session;
    private static final Set<GenerateEndpoint> CHAT_ENDPOINTS = new CopyOnWriteArraySet<>();
    private static final Set<BigInteger> GENERATED_ID = new CopyOnWriteArraySet<>();
    private static final HashMap<String, String> USERS = new HashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        this.session = session;
        CHAT_ENDPOINTS.add(this);
        USERS.put(session.getId(), username);
        log.info("Users {} connected to server with session {}", username, session.getId());
        serverMessage(Message.builder()
                .to(username).content("Connected!")
                .build());
    }
    @OnMessage
    public void onMessage(Session session, Message message) {
        if (isNull(message.getContent()) && message.getContent().isEmpty()) {
            serverMessage(Message.builder().to(USERS.get(session.getId())).content("Количество id должно быть > 0").build());
            return;
        }
        message.setFrom(USERS.get(session.getId()));
        log.info("On session {} receive message {}", session.getId(), message);
        int countGenerated = Integer.parseInt(message.getContent());
        for (int i = 0; i < countGenerated; i++) {
            log.info("Start generate {} id from {}", i, countGenerated);
            generate(message);
        }
    }
    @OnClose
    public void onClose(Session session) {
        CHAT_ENDPOINTS.remove(this);
        serverMessage(Message.builder().to(USERS.get(session.getId())).content("Disconnected!").build());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        serverMessage(Message.builder()
                .to("all").content("Unexpected error!" + throwable.getMessage())
                .build());
    }

    private static void serverMessage(Message message) {
        message.setFrom("Server");
        sendMessageAll(message);
    }

    private static void generate(Message message) {
        BigInteger randomId = new BigInteger(130, new Random());
        if (!GENERATED_ID.contains(randomId)) {
            log.info("Generate new BigDecimal: {}", randomId);
            Message generatedMessage = Message.builder().from("Server").to("all").content(("User " + message.getFrom() + " generated id: " + randomId)).build();
            sendMessageAll(generatedMessage);
        } else {
            generate(message);
        }
    }

    private static void sendMessageAll(Message message) {
        log.info("Send message: {}", message);
        CHAT_ENDPOINTS.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote()
                            .sendObject(message);
                } catch (IOException | EncodeException e) {
                    log.error("Error send message: {}", message);
                    log.error("Error: {} , stackTrace: {}", e.getMessage(), e.getStackTrace());
                }
            }
        });
    }

}
