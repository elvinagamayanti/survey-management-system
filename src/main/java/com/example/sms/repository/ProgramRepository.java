/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.sms.repository;

import com.example.sms.entity.Program;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author pinaa
 */
public interface ProgramRepository extends JpaRepository<Program, Long> {

    Program findByName(String name);
    Optional<Program> findByCode(String code);
}
