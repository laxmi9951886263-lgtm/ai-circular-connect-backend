
package app.controller;

import app.entity.Item;
import app.repository.ItemRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // Create a new item
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item createItem(@Valid @RequestBody Item item) {
        item.setId(null);
        return itemRepository.save(item);
    }

    // Get all items
    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Get one item by ID
    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item not found: " + id
                        ));
    }

    // Update an existing item
    @PutMapping("/{id}")
    public Item updateItem(
            @PathVariable Long id,
            @Valid @RequestBody Item updatedItem) {

        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item not found: " + id
                        ));

        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setClassification(updatedItem.getClassification());
        existingItem.setEstimatedValueInr(
                updatedItem.getEstimatedValueInr()
        );
        existingItem.setImageUrl(updatedItem.getImageUrl());
        existingItem.setStatus(updatedItem.getStatus());

        return itemRepository.save(existingItem);
    }

    // Delete an item
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Item not found: " + id
            );
        }

        itemRepository.deleteById(id);
    }
}
