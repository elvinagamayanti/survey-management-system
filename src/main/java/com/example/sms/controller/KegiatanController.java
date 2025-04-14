/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sms.dto.KegiatanDto;
import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Program;
import com.example.sms.entity.User;
import com.example.sms.repository.ProgramRepository;
import com.example.sms.repository.UserRepository;
import com.example.sms.service.KegiatanService;

import jakarta.validation.Valid;

/**
 *
 * @author pinaa
 */
@Controller
public class KegiatanController {
    private KegiatanService kegiatanService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgramRepository programRepository;
    
    public KegiatanController(KegiatanService kegiatanService) {
        this.kegiatanService = kegiatanService;
    }    
        
    @GetMapping("/operator/surveys")
    public String kegiatans(Model model) {
        List<KegiatanDto> kegiatanDtos = this.kegiatanService.ambilDaftarKegiatan();
        model.addAttribute("kegiatanDtos", kegiatanDtos);
        return "/operator/kegiatan";
    }    

    @GetMapping("/operator/surveys/add")
    public String addKegiatanForm(Model model) {
        KegiatanDto kegiatanDto = new KegiatanDto();

        List<Program> listPrograms = programRepository.findAll();
        
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        kegiatanDto.setUser(user);
        kegiatanDto.setSatker(user.getSatker());
        model.addAttribute("kegiatanDto", kegiatanDto);
        model.addAttribute("listPrograms", listPrograms);
        model.addAttribute("user", user);
        return "/operator/addKegiatan";
    }    
    
    @PostMapping("/operator/surveys")
    public String addKegiatan( @Valid KegiatanDto kegiatanDto, BindingResult result) {        
        if(result.hasErrors()){
            return "/operator/addKegiatan";
        }
        kegiatanService.simpanDataKegiatan(kegiatanDto);
        return "redirect:/operator/surveys";
    }

   @GetMapping("/operator/surveys/{kegiatanId}/update")
   public String updateKegiatanForm (@PathVariable("kegiatanId") Long kegiatanId,
           Model model) {
       KegiatanDto kegiatanDto = kegiatanService.cariKegiatanById(kegiatanId);
       List<Program> listPrograms = programRepository.findAll();
       model.addAttribute("kegiatanDto", kegiatanDto);
       model.addAttribute("listPrograms", listPrograms);
       return "/operator/updateKegiatan";
   }

   @PostMapping("/operator/surveys/update") 
   public String updateKegiatan (@Valid KegiatanDto kegiatanDto, BindingResult result) {
       if (result.hasErrors()) {
           return "/operator/updateKegiatan";
       }
       kegiatanService.perbaruiDataKegiatan(kegiatanDto);
       return "redirect:/operator/surveys";
   }
//    
//    @GetMapping("/operator/surveys/{kegiatanId}/delete") 
//    public String deleteKegiatan (@PathVariable("kegiatanId") Long kegiatanId) {
//        kegiatanService.hapusDataKegiatan(kegiatanId);        
//        return "redirect:/operator/surveys";
//    }

    @GetMapping("/operator/surveys/detail")
    public String viewKegiatanDetail(@RequestParam("id") Long id, Model model) {
        Kegiatan kegiatan = kegiatanService.findKegiatanById(id);
        model.addAttribute("kegiatan", kegiatan);
        return "/operator/detailKegiatan";
    }
}
