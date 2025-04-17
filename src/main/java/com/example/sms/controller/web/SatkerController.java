/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.controller.web;

import com.example.sms.dto.SatkerDto;
import com.example.sms.service.SatkerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author pinaa
 */
@Controller
public class SatkerController {
    private SatkerService satkerService;

    public SatkerController(SatkerService satkerService) {
        this.satkerService = satkerService;
    }    
        
    @GetMapping("/superadmin/satkers")
    public String satkers(Model model) {
        List<SatkerDto> satkerDtos = this.satkerService.ambilDaftarSatker();
        model.addAttribute("satkerDtos", satkerDtos);
        return "/superadmin/satkers";
    }    

    @GetMapping("/superadmin/satkers/add")
    public String addSatkerForm(Model model) {
        SatkerDto satkerDto = new SatkerDto();
        model.addAttribute("satkerDto", satkerDto);
        return "/superadmin/addSatker";
    }    
    
    @PostMapping("/superadmin/satkers")
    public String addSatker( @Valid SatkerDto satkerDto, BindingResult result) {        
        if(result.hasErrors()){
            return "/superadmin/addSatker";
        }
        satkerService.simpanDataSatker(satkerDto);
        return "redirect:/superadmin/satkers";
    }

    @GetMapping("/superadmin/satkers/{satkerId}/update")
    public String updateSatkerForm (@PathVariable("satkerId") Long satkerId,
            Model model) {
        SatkerDto satkerDto = satkerService.cariSatkerById(satkerId);
        model.addAttribute("satkerDto", satkerDto);
        return "/superadmin/updateSatker";
    }

    @PostMapping("/superadmin/satkers/update") 
    public String updateSatker (@Valid SatkerDto satkerDto, BindingResult result) {
        if (result.hasErrors()) {
            return "/superadmin/updateSatker";
        }
        satkerService.perbaruiDataSatker(satkerDto);
        return "redirect:/superadmin/satkers";
    }
    
    @GetMapping("/superadmin/satkers/{satkerId}/delete") 
    public String deleteSatker (@PathVariable("satkerId") Long satkerId) {
        satkerService.hapusDataSatker(satkerId);        
        return "redirect:/superadmin/satkers";
    }

}
