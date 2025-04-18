/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.sms.service;

import com.example.sms.dto.KegiatanDto;
import com.example.sms.entity.Kegiatan;
import java.util.List;

/**
 *
 * @author pinaa
 */
public interface KegiatanService {
    List<KegiatanDto> ambilDaftarKegiatan();
    void perbaruiDataKegiatan(KegiatanDto kegiatanDto);
    void hapusDataKegiatan(Long kegiatanId);
    void simpanDataKegiatan(KegiatanDto kegiatanDto);
    KegiatanDto cariKegiatanById(Long id);
    
    Kegiatan findKegiatanById(Long id);
}
