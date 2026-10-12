
package app.controller;

import app.entity.Item;
import app.entity.ItemImage;
import app.repository.ItemRepository;
import app.repository.ItemImageRepository;
import app.service.classifier.ImageClassifierService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/classifier")
public class ImageClassifierController {

    private final ImageClassifierService classifierService;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;

    public ImageClassifierController(
            ImageClassifierService classifierService,
            ItemRepository itemRepository,
            ItemImageRepository itemImageRepository) {
        this.classifierService = classifierService;
        this.itemRepository = itemRepository;
        this.itemImageRepository = itemImageRepository;
    }

    @PostMapping(
        value = "/analyze",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Map<String, String> analyzeImage(
            @RequestParam("image") MultipartFile image)
            throws Exception {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException(
                "Please upload an image."
            );
        }

        String contentType = image.getContentType();

        if (contentType == null || !Set.of(
                "image/jpeg",
                "image/png",
                "image/webp"
            ).contains(contentType)) {
            throw new IllegalArgumentException(
                "Please upload a JPEG, PNG, or WebP image."
            );
        }

        Map<String, String> result =
                new HashMap<>(classifierService.classifyImage(image));

        Item item = new Item();
        item.setName(result.get("itemName"));
        item.setDescription(result.get("reason"));
        item.setClassification(result.get("classification"));
        item.setEstimatedValueInr(
                new BigDecimal(result.get("estimatedValueInr"))
        );
        item.setStatus("AVAILABLE");

        Item savedItem = itemRepository.save(item);

        ItemImage itemImage = new ItemImage();
        itemImage.setItem(savedItem);
        itemImage.setImageData(image.getBytes());
        itemImage.setContentType(contentType);

        // Force Hibernate to flush the image insert to the database.
        itemImageRepository.saveAndFlush(itemImage);

        result.put("savedItemId", savedItem.getId().toString());
        result.put(
            "filename",
            image.getOriginalFilename() == null
                ? "uploaded-image"
                : image.getOriginalFilename()
        );
        result.put("contentType", contentType);
        result.put("sizeBytes", String.valueOf(image.getSize()));

        return result;
    }
}
