/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.sms.repository;

import com.example.sms.entity.Satker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author pinaa
 */
public interface SatkerRepository extends JpaRepository<Satker, Long> {
    // contoh method abstract baru. 
    Optional<Satker> findByCode(String code);
    
    // tambahkan method abstract lain disini yg bisa digunakan oleh service class jika diperlukan.
    @Query("SELECT s from Satker s WHERE " +
            " s.code LIKE CONCAT('%', :query, '%') OR " +
            " s.name LIKE CONCAT('%', :query, '%')")
    List<Satker> searchSatker(String query);

}
