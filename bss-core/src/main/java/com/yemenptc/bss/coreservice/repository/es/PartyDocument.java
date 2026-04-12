package com.yemenptc.bss.coreservice.repository.es;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.annotation.Id;
import lombok.*;

import java.util.Map;

/**
 * Elasticsearch document for party full-text search.
 */
@Document(indexName = "parties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String firstName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String lastName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String fullName;

    @Field(type = FieldType.Keyword)
    private String nationalId;

    @Field(type = FieldType.Keyword)
    private String primaryPhone;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String email;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String city;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String governorate;

    @Field(type = FieldType.Keyword)
    private String customerType;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Object)
    private Map<String, Object> characteristics;
}
