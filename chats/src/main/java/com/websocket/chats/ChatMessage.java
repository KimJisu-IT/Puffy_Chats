package com.websocket.chats;

public class ChatMessage {

    private String from;        // 메세지 발신자
    private String text;        // 메세지 내용

    public ChatMessage() {      // 기본 생성자
    }

    public ChatMessage(String from, String text) {      // 필드 초기화 생성자
        this.from = from;
        this.text = text;
    }

    public String getFrom() {       // from 필드의 getter 메서드
        return from;
    }

    public void setFrom(String from) {      // from 필드의 setter 메서드
        this.from = from;
    }

    public String getText() {       // text 필드의 getter 메서드
        return text;
    }

    public void setText(String text) {      // text 필드의 setter 메서드
        this.text = text;
    }
}
