package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.NetworkElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NetworkElementRepository extends JpaRepository<NetworkElement, String> {
    List<NetworkElement> findByType(String type);
    List<NetworkElement> findBySiteId(String siteId);
    List<NetworkElement> findByStatus(NetworkElement.ElementStatus status);
    long countByStatus(NetworkElement.ElementStatus status);
    long countByType(String type);
}
