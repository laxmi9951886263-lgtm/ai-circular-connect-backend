package app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class ChatService {

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private final RestClient client = RestClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String SYSTEM_CONTEXT = """
        You are a helpful sustainability assistant for AI Circular Connect,
        a circular economy marketplace where people can sell, donate, or
        recycle pre-loved items.

        Your role:
        - Help users decide whether an item is reusable, recyclable, or
          should be disposed of responsibly.
        - Suggest sustainable alternatives and responsible disposal methods.
        - Give tips on refurbishing, upcycling, or donating items.
        - Answer questions about categories: Furniture, Books, Electronics,
          Clothing, Home, Appliances, Toys.
        - Encourage reducing waste and making eco-friendly choices.

        Keep responses concise (2-4 sentences), practical, and friendly.
        Focus only on sustainability, circular economy, and items on the
        marketplace. Politely redirect off-topic questions.
        """;

    public String chat(String userMessage) throws Exception {
        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }

        if (userMessage.length() > 500) {
            throw new IllegalArgumentException(
                "Message is too long. Please keep it under 500 characters.");
        }

        if (apiKey == null || apiKey.isBlank()) {
            return "The AI assistant is not configured yet. " +
                   "Please contact support.";
        }

        Map<String, Object> request = Map.of(
            "system_instruction", Map.of(
                "parts", new Object[] {
                    Map.of("text", SYSTEM_CONTEXT)
                }
            ),
            "contents", new Object[] {
                Map.of(
                    "role", "user",
                    "parts", new Object[] {
                        Map.of("text", userMessage.trim())
                    }
                )
            },
            "generationConfig", Map.of(
                "maxOutputTokens", 256,
                "temperature", 0.7
            )
        );

        String response = client.post()
            .uri("https://generativelanguage.googleapis.com/v1beta/" +
                 "models/gemini-1.5-flash:generateContent")
            .header("x-goog-api-key", apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(String.class);

        if (response == null || response.isBlank()) {
            throw new IllegalStateException("AI returned an empty response.");
        }

        JsonNode root = mapper.readTree(response);
        JsonNode textNode = root.at("/candidates/0/content/parts/0/text");

        if (textNode.isMissingNode() || textNode.isNull()) {
            throw new IllegalStateException("AI returned no text response.");
        }

        return textNode.asText().trim();
    }
}
