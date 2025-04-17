package com.example.sms.repository.integration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Tahap;
import com.example.sms.entity.integration.ExternalSystemMapping;

@Repository
public interface ExternalSystemMappingRepository extends JpaRepository<ExternalSystemMapping, Long> {
    
    List<ExternalSystemMapping> findByKegiatan(Kegiatan kegiatan);
    
    List<ExternalSystemMapping> findByKegiatanAndExternalSystem(Kegiatan kegiatan, String externalSystem);
    
    Optional<ExternalSystemMapping> findByKegiatanAndTahap(Kegiatan kegiatan, Tahap tahap);
    
    @Query("SELECT esm FROM ExternalSystemMapping esm WHERE esm.kegiatan.id = :kegiatanId AND esm.tahap = :tahap")
    Optional<ExternalSystemMapping> findByKegiatanIdAndTahap(@Param("kegiatanId") Long kegiatanId, @Param("tahap") Tahap tahap);
    
    @Query("SELECT esm FROM ExternalSystemMapping esm WHERE esm.kegiatan.id = :kegiatanId AND esm.externalSystem = :externalSystem")
    List<ExternalSystemMapping> findByKegiatanIdAndExternalSystem(@Param("kegiatanId") Long kegiatanId, @Param("externalSystem") String externalSystem);
    
    @Query("SELECT DISTINCT esm.externalSystem FROM ExternalSystemMapping esm WHERE esm.kegiatan.id = :kegiatanId")
    List<String> findDistinctExternalSystemsByKegiatanId(@Param("kegiatanId") Long kegiatanId);
    
    boolean existsByKegiatanAndTahap(Kegiatan kegiatan, Tahap tahap);
}