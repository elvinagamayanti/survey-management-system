/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.controller;

import com.example.sms.dto.ProvinceDto;
import com.example.sms.dto.SatkerDto;
import com.example.sms.service.ProvinceService;
import com.example.sms.service.SatkerService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sms.entity.Province;
import com.example.sms.entity.Satker;

/**
 *
 * @author pinaa
 */
@Controller
public class ProvinceController {
    private ProvinceService provinceService;

    public ProvinceController(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    @GetMapping("/superadmin/provinces")
    public String provinces(Model model) {
        List<ProvinceDto> provinceDtos = this.provinceService.ambilDaftarProvinsi();
        model.addAttribute("provinceDtos", provinceDtos);
        return "/superadmin/provinces";
    }

    @GetMapping("/superadmin/provinces/add")
    public String addProvinceForm(Model model) {
        ProvinceDto provinceDto = new ProvinceDto();
        model.addAttribute("provinceDto", provinceDto);
        return "/superadmin/addProvince";
    }

    @PostMapping("/superadmin/provinces")
    public String addProvince(@Valid ProvinceDto provinceDto, BindingResult result) {
        if (result.hasErrors()) {
            return "/superadmin/addProvince";
        }
        provinceService.simpanDataProvinsi(provinceDto);
        return "redirect:/superadmin/provinces";
    }

//    @GetMapping("/superadmin/provinces/{provinceId}/update")
//    public String updateProvinceForm(@PathVariable("provinceId") Long provinceId,
//                                     Model model) {
//        ProvinceDto provinceDto = provinceService.cariProvinceById(provinceId);
//        model.addAttribute("provinceDto", provinceDto);
//        return "/superadmin/updateProvince";
//    }

//    @PostMapping("/superadmin/provinces/update")
//    public String updateProvince(@Valid ProvinceDto provinceDto, BindingResult result) {
//        if (result.hasErrors()) {
//            return "/superadmin/updateProvince";
//        }
//        provinceService.perbaruiDataProvince(provinceDto);
//        return "redirect:/superadmin/provinces";
//    }
//
//    @GetMapping("/superadmin/provinces/{provinceId}/delete")
//    public String deleteProvince(@PathVariable("provinceId") Long provinceId) {
//        provinceService.hapusDataProvince(provinceId);
//        return "redirect:/superadmin/provinces";
//    }

    @GetMapping("/superadmin/provinces/{provinceCode}/satkers")
    public String getSatkersByProvince(@PathVariable("provinceCode") String provinceCode, Model model) {
        List<Satker> satkers = provinceService.getSatkersByProvinceCode(provinceCode);
        // ProvinceDto province = provinceService.cariProvinceByCode(provinceCode);
        model.addAttribute("satkers", satkers);
        model.addAttribute("provinceCode", provinceCode);
        // model.addAttribute("province", province);
        return "/superadmin/satkersByProvince"; 
    }
}
