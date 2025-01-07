/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.sms.repository;

import com.example.sms.entity.Kegiatan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author pinaa
 */
public interface KegiatanRepository extends JpaRepository<Kegiatan, Long> {
    // contoh method abstract baru. 
    Optional<Kegiatan> findByCode(String code);
    
    // tambahkan method abstract lain disini yg bisa digunakan oleh service class jika diperlukan.
//    @Query("SELECT k from Kegiatan k WHERE " +
//            " k.name LIKE CONCAT('%', :query, '%') OR " +
//            " k.id LIKE CONCAT('%', :query, '%')")
//    List<Kegiatan> searchKegiatan(String query);

}
