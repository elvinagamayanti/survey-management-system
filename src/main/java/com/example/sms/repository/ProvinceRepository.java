/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.sms.repository;

import com.example.sms.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author pinaa
 */
public interface ProvinceRepository extends JpaRepository<Province, Long> {

    Province findByName(String name);
    Province findByCode(String code);
}
