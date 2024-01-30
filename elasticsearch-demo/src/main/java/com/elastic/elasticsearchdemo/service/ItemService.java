package com.elastic.elasticsearchdemo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elastic.elasticsearchdemo.dto.SearchRequestDto;
import com.elastic.elasticsearchdemo.model.Item;
import com.elastic.elasticsearchdemo.repository.ItemReposiyory;
import com.elastic.elasticsearchdemo.util.EsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemReposiyory itemReposiyory;
    private final JsonDataService jsonDataService;
    private final ElasticsearchClient elasticsearchClient;


    public Item createindex(Item item) {

        return itemReposiyory.save(item);

    }

    public void addItemsFromJson() {
        log.info("Adding items ftom json");
        List<Item> itemList = jsonDataService.readItemsFromJson();
        itemReposiyory.saveAll(itemList);

    }

    public List<Item> getAllDataFromIndex(String indexName) {
        var query = EsUtil.createMatchAllQuery();
        log.info("Elasticsearch query {}", query.toString());
        SearchResponse<Item> response = null;
        try {
            response = elasticsearchClient.search(
                    q -> q.index(indexName).query(query), Item.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Elasticsearch response {}", response);
        return extractItemsFromResponse(response);
    }

    public List<Item> extractItemsFromResponse(SearchResponse<Item> response) {
        return response
                .hits()
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<Item> searchItemsByFieldAndValue(SearchRequestDto dto) {

        Supplier<Query> query = EsUtil.buildQueryForFieldAndValue(dto.getFieldName().get(0),
                dto.getSearchValue().get(0));
        log.info("Elasticsearch query {}", query.toString());

        SearchResponse<Item> response = null;

        try {
            response = elasticsearchClient.search(q->q.index("items_index").query(query.get()),Item.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Elasticsearch response {}", response);
        return extractItemsFromResponse(response);


    }

    public List<Item> searchItemsByNameAndBrandWithQuery(String name, String brand) {

        return itemReposiyory.searchByNameAndBrand(name,brand);

    }

    public List<Item> boolQueryFieldAndValue(SearchRequestDto searchRequestDto) {
        try {
            var supplier = EsUtil.createBoolQuery(searchRequestDto);
            log.info("Elasticsearch query: " + supplier.get().toString());

            SearchResponse<Item> response = elasticsearchClient.search(q ->
                    q.index("items_index").query(supplier.get()), Item.class);
            log.info("Elasticsearch response: {}", response.toString());

            return extractItemsFromResponse(response);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public Set<String> findSuggestedItemNames(String itemName) {
        Query autoSuggestQuery = EsUtil.buildAutoSuggestQuery(itemName);
        log.info("Elasticsearch query: {}", autoSuggestQuery.toString());

        try {
            return elasticsearchClient.search(q -> q.index("items_index").query(autoSuggestQuery), Item.class)
                    .hits()
                    .hits()
                    .stream()
                    .map(Hit::source)
                    .map(Item::getName)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> autoSuggestItemsByNameWithQuery(String name) {
        List<Item> items = itemReposiyory.customAutocompleteSearch(name);
        log.info("Elasticsearch response: {}", items.toString());
        return items
                .stream()
                .map(Item::getName)
                .collect(Collectors.toList());
    }
}
