package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.NumberPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface NumberPoolRepository extends JpaRepository<NumberPool, String> {
    Optional<NumberPool> findByNumber(String number);
    List<NumberPool> findByStatus(NumberPool.NumberStatus status);
    List<NumberPool> findByNumberType(NumberPool.NumberType numberType);
    List<NumberPool> findByExchange(String exchange);
    List<NumberPool> findByNumberTypeAndStatusAndExchange(NumberPool.NumberType type, NumberPool.NumberStatus status, String exchange);
    @Query("SELECT n FROM NumberPool n WHERE n.numberType = :type AND n.exchange = :exchange AND n.status = 'AVAILABLE' ORDER BY RAND() LIMIT 1")
    Optional<NumberPool> findFirstAvailable(@Param("type") NumberPool.NumberType type, @Param("exchange") String exchange);
    @Query("SELECT n FROM NumberPool n WHERE n.numberType = :type AND n.exchange = :exchange AND n.status = 'AVAILABLE'")
    List<NumberPool> findAvailableNumbers(@Param("type") NumberPool.NumberType type, @Param("exchange") String exchange);
    @Query("UPDATE NumberPool n SET n.status = 'AVAILABLE', n.reservationId = null, n.reservationExpiry = null WHERE n.status = 'RESERVED' AND n.reservationExpiry < :now")
    int releaseExpiredReservations(@Param("now") Instant now);
}
