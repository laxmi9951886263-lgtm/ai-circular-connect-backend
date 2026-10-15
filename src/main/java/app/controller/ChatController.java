package app.controller;

import app.service.ChatService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody ChatRequest request) throws Exception {

        String reply = chatService.chat(request.message());
        return ResponseEntity.ok(Map.of("reply", reply));
    }

    public record ChatRequest(
        @NotBlank(message = "Message is required")
        @Size(max = 500, message = "Message must be under 500 characters")
        String message
    ) {}
}
