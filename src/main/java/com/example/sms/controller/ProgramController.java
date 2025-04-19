// /*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
// to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit
// this template
// */
// package com.example.sms.controller;

// import java.util.List;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;

// import com.example.sms.dto.ProgramDto;
// import com.example.sms.service.ProgramService;

// import jakarta.validation.Valid;

// /**
// *
// * @author pinaa
// */
// @Controller
// public class ProgramController {
// private ProgramService programService;

// public ProgramController(ProgramService programService) {
// this.programService = programService;
// }

// @GetMapping("/superadmin/programs")
// public String programs(Model model) {
// List<ProgramDto> programDtos = this.programService.ambilDaftarProgram();
// model.addAttribute("programDtos", programDtos);
// return "/superadmin/programs";
// }

// @GetMapping("/superadmin/outputs")
// public String outputs(Model model) {
// List<ProgramDto> programDtos = this.programService.ambilDaftarProgram();
// model.addAttribute("programDtos", programDtos);
// return "/superadmin/outputs";
// }

// @GetMapping("/superadmin/programs/add")
// public String addProgramForm(Model model) {
// ProgramDto programDto = new ProgramDto();
// model.addAttribute("programDto", programDto);
// return "/superadmin/addProgram";
// }

// @PostMapping("/superadmin/programs")
// public String addProgram(@Valid ProgramDto programDto, BindingResult result)
// {
// if (result.hasErrors()) {
// return "/superadmin/addProgram";
// }
// programService.simpanDataProgram(programDto);
// return "redirect:/superadmin/programs";
// }

// @GetMapping("/superadmin/programs/{programId}/update")
// public String updateProgramForm(@PathVariable("programId") Long programId,
// Model model) {
// ProgramDto programDto = programService.cariProgramById(programId);
// model.addAttribute("programDto", programDto);
// return "/superadmin/updateProgram";
// }

// @PostMapping("/superadmin/programs/update")
// public String updateProgram(@Valid ProgramDto programDto, BindingResult
// result) {
// if (result.hasErrors()) {
// return "/superadmin/updateProgram";
// }
// programService.perbaruiDataProgram(programDto);
// return "redirect:/superadmin/programs";
// }

// @GetMapping("/superadmin/programs/{programId}/delete")
// public String deleteProgram(@PathVariable("programId") Long programId) {
// programService.hapusDataProgram(programId);
// return "redirect:/superadmin/programs";
// }
// }