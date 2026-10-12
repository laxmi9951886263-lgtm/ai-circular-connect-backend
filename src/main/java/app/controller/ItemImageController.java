
package app.controller;

import app.entity.ItemImage;
import app.repository.ItemImageRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemImageController {

    private final ItemImageRepository itemImageRepository;

    public ItemImageController(
            ItemImageRepository itemImageRepository) {
        this.itemImageRepository = itemImageRepository;
    }

    @GetMapping("/{itemId}/image")
    public ResponseEntity<byte[]> getItemImage(
            @PathVariable Long itemId) {

        ItemImage itemImage = itemImageRepository
                .findByItemId(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Image not found for item ID: "
                                        + itemId));

        MediaType mediaType = MediaType.parseMediaType(
                itemImage.getContentType());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        mediaType.toString())
                .body(itemImage.getImageData());
    }
}
