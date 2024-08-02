package com.websocket.chats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ChatMessageHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("Connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("Connection closed: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received message: " + payload);

        if (isChatMessage(payload)) {
            try {
                ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
                broadcast(chatMessage);
            } catch (IOException e) {
                logger.error("Error handling message: " + payload, e);
                session.sendMessage(new TextMessage("Error processing your message"));
            }
        } else {
            logger.info("Ignoring non-chat message: " + payload);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Transport error in session: " + session.getId(), exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    private void broadcast(ChatMessage message) throws IOException {
        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
        synchronized (sessions) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        }
    }

    private boolean isChatMessage(String payload) {
        // Add your logic here to determine if the payload is a chat message.
        // For simplicity, we'll just check if it starts with a "{" character.
        return payload.trim().startsWith("{");
    }
}
