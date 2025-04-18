/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.sms.repository;

import com.example.sms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author pinaa
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
