package com.example.fitup;

import java.util.List;

public class GroqModels {

    public static class Request {
        public String model;
        public List<Message> messages;

        public Request(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    public static class Response {
        public List<Choice> choices;
    }

    public static class Choice {
        public Message message;
    }
}