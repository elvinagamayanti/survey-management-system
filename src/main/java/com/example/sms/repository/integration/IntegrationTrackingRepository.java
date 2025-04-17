package com.example.sms.repository.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.integration.IntegrationTracking;

@Repository
public interface IntegrationTrackingRepository extends JpaRepository<IntegrationTracking, Long> {
    
    List<IntegrationTracking> findByKegiatan(Kegiatan kegiatan);
    
    Optional<IntegrationTracking> findByKegiatanAndExternalSystem(Kegiatan kegiatan, String externalSystem);
    
    @Query("SELECT it FROM IntegrationTracking it WHERE it.kegiatan.id = :kegiatanId AND it.externalSystem = :externalSystem")
    Optional<IntegrationTracking> findByKegiatanIdAndExternalSystem(@Param("kegiatanId") Long kegiatanId, @Param("externalSystem") String externalSystem);
    
    @Query("SELECT it FROM IntegrationTracking it WHERE it.lastSyncTime < :cutoffTime AND it.syncStatus != 'SUCCESS'")
    List<IntegrationTracking> findFailedSyncsBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(it) FROM IntegrationTracking it WHERE it.kegiatan.id = :kegiatanId AND it.syncStatus = 'SUCCESS'")
    long countSuccessfulSyncsByKegiatanId(@Param("kegiatanId") Long kegiatanId);
    
    @Query("SELECT it FROM IntegrationTracking it WHERE it.lastSyncTime < :cutoffTime")
    List<IntegrationTracking> findSyncsDueForRefresh(@Param("cutoffTime") LocalDateTime cutoffTime);
}