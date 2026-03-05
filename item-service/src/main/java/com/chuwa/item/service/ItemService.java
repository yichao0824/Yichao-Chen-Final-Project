package com.chuwa.item.service;

import com.chuwa.item.dto.CreateItemRequest;
import com.chuwa.item.dto.ItemResponse;
import com.chuwa.item.entity.Item;
import com.chuwa.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemResponse create(CreateItemRequest req) {
        Item item = Item.builder()
                .id(req.getId())
                .name(req.getName())
                .price(req.getPrice())
                .upc(req.getUpc())
                .pictureUrls(req.getPictureUrls())
                .inventory(req.getInventory())
                .attributes(req.getAttributes())
                .build();
        itemRepository.save(item);
        return toResponse(item);
    }

    private ItemResponse toResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .upc(item.getUpc())
                .pictureUrls(item.getPictureUrls())
                .inventory(item.getInventory())
                .attributes(item.getAttributes())
                .build();
    }
    public ItemResponse getById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        return toResponse(item);
    }
    public Integer getInventory(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"))
                .getInventory();
    }
    public Integer increaseInventory(String id, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Qty must be greater than 0");
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.setInventory(item.getInventory() + qty);
        itemRepository.save(item);
        return item.getInventory();
    }
    public Integer decreaseInventory(String id, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Qty must be greater than 0");
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (item.getInventory() < qty) throw new IllegalArgumentException("Not enough inventory");
        item.setInventory(item.getInventory() - qty);
        itemRepository.save(item);
        return item.getInventory();
    }
}
