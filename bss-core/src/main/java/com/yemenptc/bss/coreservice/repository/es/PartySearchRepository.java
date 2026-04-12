package com.yemenptc.bss.coreservice.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for party full-text search.
 */
@Repository
public interface PartySearchRepository extends ElasticsearchRepository<PartyDocument, String> {

    List<PartyDocument> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

    List<PartyDocument> findByNationalId(String nationalId);

    List<PartyDocument> findByPrimaryPhone(String phone);

    List<PartyDocument> findByCity(String city);

    List<PartyDocument> findByCustomerType(String customerType);

    List<PartyDocument> findByFullNameContaining(String name);
}
