// /*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
// to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit
// this template
// */
// package com.example.sms.controller;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;

// import com.example.sms.dto.KegiatanDto;
// import com.example.sms.service.KegiatanService;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;

// /**
// *
// * @author pinaa
// */

// @Controller
// public class HomeController {

// @Autowired
// private KegiatanService kegiatanService;

// @GetMapping("/")
// public String index(Model model) throws JsonProcessingException {
// List<KegiatanDto> kegiatanList = kegiatanService.ambilDaftarKegiatan();

// // === CHART 2: JUMLAH PER SATKER ===
// Map<String, Long> countPerSatker = kegiatanList.stream()
// .collect(Collectors.groupingBy(
// k -> k.getSatker().getName(),
// Collectors.counting()
// ));

// List<String> labelsSatker = new ArrayList<>(countPerSatker.keySet());
// List<Long> dataSatker = new ArrayList<>(countPerSatker.values());

// model.addAttribute("labelsSatker", new
// ObjectMapper().writeValueAsString(labelsSatker));
// model.addAttribute("dataSatker", new
// ObjectMapper().writeValueAsString(dataSatker));
// model.addAttribute("hasDataSatker", !labelsSatker.isEmpty());

// // === CHART 1: JUMLAH PER PROGRAM ===
// // Map<String, Long> countPerProgram = kegiatanList.stream()
// // .collect(Collectors.groupingBy(
// // k -> k.getProgram().getName(),
// // Collectors.counting()
// // ));

// // List<String> labelsProgram = new ArrayList<>(countPerProgram.keySet());
// // List<Long> dataProgram = new ArrayList<>(countPerProgram.values());

// // model.addAttribute("labelsProgram", new
// ObjectMapper().writeValueAsString(labelsProgram));
// // model.addAttribute("dataProgram", new
// ObjectMapper().writeValueAsString(dataProgram));
// // model.addAttribute("hasDataProgram", !labelsProgram.isEmpty());

// return "index";
// }

// @GetMapping("/kegiatan")
// public String kegiatanPage() {
// return "kegiatan";
// }

// @GetMapping("/manajemenPengguna")
// public String manajemenPenggunaPage() {
// return "manajemenPengguna";
// }

// @GetMapping("/addKegiatan")
// public String addKegiatanPage() {
// return "addKegiatan";
// }

// @GetMapping("/updateKegiatan")
// public String updateKegiatanPage() {
// return "updateKegiatan";
// }

// @GetMapping("/detailKegiatan")
// public String detailKegiatanPage() {
// return "detailKegiatan";
// }

// @GetMapping("/addPengguna")
// public String addPenggunaPage() {
// return "addPengguna";
// }

// @GetMapping("/updatePengguna")
// public String updatePenggunaPage() {
// return "updatePengguna";
// }

// @GetMapping("/detailPengguna")
// public String detailPenggunaPage() {
// return "detailPengguna";
// }

// @GetMapping("/manajemenSatker")
// public String manajemenSatkerPage() {
// return "manajemenSatker";
// }

// @GetMapping("/addSatker")
// public String addSatkerPage() {
// return "addSatker";
// }

// @GetMapping("/updateSatker")
// public String updateSatkerPage() {
// return "updateSatker";
// }

// @GetMapping("/detailProfil")
// public String detailProfilPage() {
// return "detailProfil";
// }

// @GetMapping("/ubahPassword")
// public String ubahPasswordPage() {
// return "ubahPassword";
// }

// @GetMapping("/pengaduan")
// public String pengaduanPage() {
// return "operator/pengaduan";
// }

// @GetMapping("/addPengaduan")
// public String addPengaduanPage() {
// return "addPengaduan";
// }

// @GetMapping("/chatDesk")
// public String chatDeskPage() {
// return "chatDesk";
// }

// @GetMapping("/faq")
// public String faqPage() {
// return "faq";
// }
// }
