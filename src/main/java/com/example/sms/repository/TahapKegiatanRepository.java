package com.example.sms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Tahap;
import com.example.sms.entity.TahapKegiatan;

public interface TahapKegiatanRepository extends JpaRepository<TahapKegiatan, Long> {
    
    List<TahapKegiatan> findByKegiatanOrderByTahap(Kegiatan kegiatan);
    
    Optional<TahapKegiatan> findByKegiatanAndTahap(Kegiatan kegiatan, Tahap tahap);
    
    @Query("SELECT tk FROM TahapKegiatan tk WHERE tk.kegiatan.id = :kegiatanId AND tk.isActive = true")
    Optional<TahapKegiatan> findActiveStepByKegiatanId(@Param("kegiatanId") Long kegiatanId);
    
    @Query("SELECT COUNT(tk) FROM TahapKegiatan tk WHERE tk.kegiatan.id = :kegiatanId AND tk.isCompleted = true")
    int countCompletedStepsByKegiatanId(@Param("kegiatanId") Long kegiatanId);
}