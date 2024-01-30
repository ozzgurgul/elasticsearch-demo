package com.elastic.elasticsearchdemo.controller;

import com.elastic.elasticsearchdemo.dto.SearchRequestDto;
import com.elastic.elasticsearchdemo.model.Item;
import com.elastic.elasticsearchdemo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Item createIndex(@RequestBody Item item) {
        return itemService.createindex(item);
    }

    @PostMapping("/init-index")
    public void addItemsFromJson() {
        itemService.addItemsFromJson();
    }

    @GetMapping("/getAllDataFromIndex/{indexName}")
    public List<Item> getAllDataFromIndex(@PathVariable String indexName) {
        return itemService.getAllDataFromIndex(indexName);
    }


    @GetMapping("/search")
    public List<Item> searchItemsByFieldAndValue(@RequestBody SearchRequestDto dto){
        return itemService.searchItemsByFieldAndValue(dto);
    }

    @GetMapping("/search/{name}/{brand}")
    public List<Item> searchItemsByNameAndBrandWithQuery(@PathVariable String name,
                                                         @PathVariable String brand){
        return itemService.searchItemsByNameAndBrandWithQuery(name,brand);
    }

    @GetMapping("/boolQuery")
    public List<Item> boolQuery(@RequestBody SearchRequestDto searchRequestDto) {
        return itemService.boolQueryFieldAndValue(searchRequestDto);
    }

    @GetMapping("/autoSuggest/{name}")
    public Set<String> autoSuggestItemsByName(@PathVariable String name) {
        return itemService.findSuggestedItemNames(name);
    }

    @GetMapping("/suggestionsQuery/{name}")
    public List<String> autoSuggestItemsByNameWithQuery(@PathVariable String name) {
        return itemService.autoSuggestItemsByNameWithQuery(name);
    }

}
