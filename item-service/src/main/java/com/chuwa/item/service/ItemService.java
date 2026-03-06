package com.chuwa.item.service;

import com.chuwa.item.dto.CreateItemRequest;
import com.chuwa.item.dto.ItemResponse;
import com.chuwa.item.dto.UpdateItemRequest;
import com.chuwa.item.entity.Item;
import com.chuwa.item.exception.ApiException;
import com.chuwa.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final MongoTemplate mongoTemplate;

    public ItemResponse create(CreateItemRequest req) {
        if (itemRepository.existsById(req.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Item id already exists");
        }

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

    public List<ItemResponse> getAll() {
        return itemRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ItemResponse getById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item not found"));
        return toResponse(item);
    }

    public ItemResponse update(String id, UpdateItemRequest req) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item not found"));

        item.setName(req.getName());
        item.setPrice(req.getPrice());
        item.setUpc(req.getUpc());
        item.setPictureUrls(req.getPictureUrls());
        item.setAttributes(req.getAttributes());

        itemRepository.save(item);
        return toResponse(item);
    }

    public Integer getInventory(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item not found"))
                .getInventory();
    }

    public Integer increaseInventory(String id, int qty) {
        if (qty <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Qty must be greater than 0");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Update update = new Update().inc("inventory", qty);

        Item updatedItem = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Item.class
        );

        if (updatedItem == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Item not found");
        }

        return updatedItem.getInventory();
    }
    public Integer decreaseInventory(String id, int qty) {
        if (qty <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Qty must be greater than 0");
        }

        Query query = new Query();
        query.addCriteria(
                Criteria.where("_id").is(id)
                        .and("inventory").gte(qty)
        );

        Update update = new Update().inc("inventory", -qty);

        Item updatedItem = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Item.class
        );

        if (updatedItem == null) {
            boolean itemExists = itemRepository.existsById(id);
            if (!itemExists) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Item not found");
            }
            throw new ApiException(HttpStatus.CONFLICT, "Not enough inventory");
        }

        return updatedItem.getInventory();
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
}