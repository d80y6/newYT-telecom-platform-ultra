package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, UUID> {
    
    List<Alarm> findByStatus(Alarm.AlarmStatus status);
    
    List<Alarm> findBySeverity(Alarm.AlarmSeverity severity);
    
    List<Alarm> findByResourceId(UUID resourceId);
    
    List<Alarm> findByAlarmType(String alarmType);
    
    long countBySeverity(Alarm.AlarmSeverity severity);
    
    long countByStatus(Alarm.AlarmStatus status);
}
