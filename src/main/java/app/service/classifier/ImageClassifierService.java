
package app.service.classifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Map;
import java.util.Set;

@Service
public class ImageClassifierService {

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private final RestClient client = RestClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, String> classifyImage(MultipartFile image)
            throws Exception {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException(
                "Please upload an image."
            );
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "Gemini API key is missing. Configure GEMINI_API_KEY."
            );
        }

        String mimeType = image.getContentType();

        if (mimeType == null || !Set.of(
                "image/jpeg", "image/png", "image/webp"
            ).contains(mimeType)) {
            throw new IllegalArgumentException(
                "Please upload a JPEG, PNG, or WebP image."
            );
        }

        String prompt = """
            Analyze this image of a second-hand item for a circular
            economy marketplace.

            Return ONLY valid JSON with these fields:
            classification, itemName, reason, estimatedValueInr.

            classification must be exactly one of:
            REUSABLE, RECYCLABLE, UNCERTAIN.

            REUSABLE means the item appears suitable for further use.
            RECYCLABLE means it appears more suitable for recycling.
            UNCERTAIN means the image is insufficient to decide.

            estimatedValueInr must be a numeric estimate in INR,
            or 0 if the value cannot reasonably be estimated.
            Estimate from visible evidence only. Do not claim the
            item's actual condition can be fully verified from a photo.
            """;

        String base64 = Base64.getEncoder()
                .encodeToString(image.getBytes());

        Map<String, Object> request = Map.of(
            "contents", new Object[] {
                Map.of("parts", new Object[] {
                    Map.of("text", prompt),
                    Map.of("inline_data", Map.of(
                        "mime_type", mimeType,
                        "data", base64
                    ))
                })
            },
            "generationConfig", Map.of(
                "responseMimeType", "application/json"
            )
        );

        String response = client.post()
            .uri("https://generativelanguage.googleapis.com/v1beta/"
               + "models/gemini-1.5-flash:generateContent")
            .header("x-goog-api-key", apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(String.class);

        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                "Gemini returned an empty response."
            );
        }

        JsonNode root = mapper.readTree(response);
        JsonNode textNode = root.at(
            "/candidates/0/content/parts/0/text"
        );

        if (textNode.isMissingNode() || textNode.isNull()) {
            throw new IllegalStateException(
                "The AI model returned no classification."
            );
        }

        JsonNode result = mapper.readTree(textNode.asText());

        String classification =
            result.path("classification").asText("UNCERTAIN");

        if (!Set.of(
                "REUSABLE", "RECYCLABLE", "UNCERTAIN"
            ).contains(classification)) {
            classification = "UNCERTAIN";
        }

        String itemName =
            result.path("itemName").asText("Unknown item");

        String reason =
            result.path("reason").asText("No reason provided");

        String estimatedValue =
            result.path("estimatedValueInr").asText("0");

        return Map.of(
            "classification", classification,
            "itemName", itemName,
            "reason", reason,
            "estimatedValueInr", estimatedValue
        );
    }
}
