/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.controller;

import com.example.sms.dto.RoleDto;
import com.example.sms.dto.SatkerDto;
import com.example.sms.service.RoleService;
import com.example.sms.service.SatkerService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sms.entity.Role;
import com.example.sms.entity.User;

/**
 *
 * @author pinaa
 */
@Controller
public class RoleController {
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/superadmin/roles")
    public String roles(Model model) {
        List<RoleDto> roleDtos = this.roleService.ambilDaftarRole();
        model.addAttribute("roleDtos", roleDtos);
        return "/superadmin/roles";
    }

    @GetMapping("/superadmin/roles/add")
    public String addRoleForm(Model model) {
        RoleDto roleDto = new RoleDto();
        model.addAttribute("roleDto", roleDto);
        return "/superadmin/addRole";
    }

    @PostMapping("/superadmin/roles")
    public String addRole(@Valid RoleDto roleDto, BindingResult result) {
        if (result.hasErrors()) {
            return "/superadmin/addRole";
        }
        roleService.simpanDataRole(roleDto);
        return "redirect:/superadmin/roles";
    }

//    @GetMapping("/superadmin/roles/{roleId}/update")
//    public String updateRoleForm(@PathVariable("roleId") Long roleId,
//                                 Model model) {
//        RoleDto roleDto = roleService.cariRoleById(roleId);
//        model.addAttribute("roleDto", roleDto);
//        return "/superadmin/updateRole";
//    }

//    @PostMapping("/superadmin/roles/update")
//    public String updateRole(@Valid RoleDto roleDto, BindingResult result) {
//        if (result.hasErrors()) {
//            return "/superadmin/updateRole";
//        }
//        roleService.perbaruiDataRole(roleDto);
//        return "redirect:/superadmin/roles";
//    }
//
//    @GetMapping("/superadmin/roles/{roleId}/delete")
//    public String deleteRole(@PathVariable("roleId") Long roleId) {
//        roleService.hapusDataRole(roleId);
//        return "redirect:/superadmin/roles";
//    }

    @GetMapping("/superadmin/roles/{roleId}/users")
    public String getUsersByRole(@PathVariable("roleId") Long roleId, Model model) {
        List<User> users = roleService.getUsersByRoleId(roleId);
        RoleDto role = roleService.cariRoleById(roleId);
        model.addAttribute("users", users);
        model.addAttribute("roleId", roleId);
        model.addAttribute("role", role);
        return "/superadmin/usersByRole"; // Pastikan file ini ada di folder templates
    }
}
