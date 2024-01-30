package com.elastic.elasticsearchdemo.util;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.util.ObjectBuilder;
import com.elastic.elasticsearchdemo.dto.SearchRequestDto;
import lombok.experimental.UtilityClass;

import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class EsUtil {
    public static Query createMatchAllQuery() {
        return Query.of(q->q.matchAll(new MatchAllQuery.Builder().build()));
    }

    public static Supplier<Query> buildQueryForFieldAndValue(String fieldName, String searchValue) {
        return () -> Query.of(q->q.match(buildMatchQueryForFieldAndValue(fieldName,searchValue)));
    }

    private static MatchQuery buildMatchQueryForFieldAndValue(String fieldName, String searchValue) {

        return new MatchQuery.Builder()
                .field(fieldName)
                .query(searchValue)
                .build();

    }

    public static Supplier<Query> createBoolQuery(SearchRequestDto searchRequestDto) {

    return () -> Query.of(q-> q.bool(boolQuery(searchRequestDto.getFieldName().get(0),searchRequestDto.getSearchValue().get(0),
            searchRequestDto.getFieldName().get(1),searchRequestDto.getSearchValue().get(1))));
    }

    public static BoolQuery boolQuery(String key1, String value1, String key2, String value2) {
        return new BoolQuery.Builder()
                .filter(termQuery(key1.toString(), value1))
                .must(matchQuery(key2.toString(), value2))
                .build();
    }

    public static Query termQuery(String field, String value) {
        return Query.of(q -> q.term(new TermQuery.Builder()
                .field(field)
                .value(value)
                .build()));
    }

    public static Query matchQuery(String field, String value) {
        return Query.of(q -> q.match(new MatchQuery.Builder()
                .field(field)
                .query(value)
                .build()));
    }

    public static Query buildAutoSuggestQuery(String name) {
        return Query.of(q -> q.match(createAutoSuggestMatchQuery(name)));
    }
    public static MatchQuery createAutoSuggestMatchQuery(String name) {
        return new MatchQuery.Builder()
                .field("name")
                .query(name)
                .analyzer("custom_index")
                .build();
    }
}
