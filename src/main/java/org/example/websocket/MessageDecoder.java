package org.example.websocket;

import com.google.gson.Gson;
import jakarta.websocket.Decoder;
import org.example.model.Message;

public class MessageDecoder implements Decoder.Text<Message> {
    protected static final Gson gson = new Gson();
    @Override
    public Message decode(String s) {
        return gson.fromJson(s, Message.class);
    }
    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

}
