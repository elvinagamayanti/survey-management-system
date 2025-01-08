/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.sms.service;

import com.example.sms.dto.RoleDto;
import com.example.sms.entity.User;

import java.util.List;

/**
 *
 * @author pinaa
 */
public interface RoleService {
    List<RoleDto> ambilDaftarRole();
    void perbaruiDataRole(RoleDto roleDto);
    void hapusDataRole(Long roleId);
    void simpanDataRole(RoleDto roleDto);
    RoleDto cariRoleById(Long id);
    List<User> getUsersByRoleId(Long roleId);
}
