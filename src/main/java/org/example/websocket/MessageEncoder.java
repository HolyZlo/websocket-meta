package org.example.websocket;


import com.google.gson.Gson;
import jakarta.websocket.Encoder;
import org.example.model.Message;

public class MessageEncoder implements Encoder.Text<Message> {
    private static final Gson gson = new Gson();

    @Override
    public String encode(Message message) {
        return gson.toJson(message);
    }
}
