package com.example.fitup;

import java.util.List;

public class GeminiModels {
    public static class ChatMessage {
        public String message;
        public boolean isUser; //true/false <-> user/chatbox

        public ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
        }
    }

    public static class Request {
        public List<Content> contents;

        public Request(List<Content> contents) {
            this.contents = contents;
        }
    }

    public static class Content {
        public String role;
        public List<Part> parts;

        public Content(String role, List<Part> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }

    public static class Response {
        public List<Candidate> candidates;
    }

    public static class Candidate {
        public Content content;
    }
}