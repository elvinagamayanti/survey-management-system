/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 *
 * @author pinaa
 */

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/kegiatan")
    public String kegiatanPage() {
        return "kegiatan";
    }
    
    @GetMapping("/manajemenPengguna")
    public String manajemenPenggunaPage() {
        return "manajemenPengguna";
    }
    
    @GetMapping("/addKegiatan")
    public String addKegiatanPage() {
        return "addKegiatan";
    }
    
    @GetMapping("/updateKegiatan")
    public String updateKegiatanPage() {
        return "updateKegiatan";
    }
    
    @GetMapping("/detailKegiatan")
    public String detailKegiatanPage() {
        return "detailKegiatan";
    }

    @GetMapping("/addPengguna")
    public String addPenggunaPage() {
        return "addPengguna";
    }
    
    @GetMapping("/updatePengguna")
    public String updatePenggunaPage() {
        return "updatePengguna";
    }
    
    @GetMapping("/detailPengguna")
    public String detailPenggunaPage() {
        return "detailPengguna";
    }
    
    @GetMapping("/manajemenSatker")
    public String manajemenSatkerPage() {
        return "manajemenSatker";
    }

    @GetMapping("/addSatker")
    public String addSatkerPage() {
        return "addSatker";
    }

    @GetMapping("/updateSatker")
    public String updateSatkerPage() {
        return "updateSatker";
    }

    @GetMapping("/detailProfil")
    public String detailProfilPage() {
        return "detailProfil";
    }

    @GetMapping("/ubahPassword")
    public String ubahPasswordPage() {
        return "ubahPassword";
    }

    @GetMapping("/pengaduan")
    public String pengaduanPage() {
        return "operator/pengaduan";
    }
    
    @GetMapping("/addPengaduan")
    public String addPengaduanPage() {
        return "addPengaduan";
    }

    @GetMapping("/chatDesk")
    public String chatDeskPage() {
        return "chatDesk";
    }

    @GetMapping("/faq")
    public String faqPage() {
        return "faq";
    }
}
