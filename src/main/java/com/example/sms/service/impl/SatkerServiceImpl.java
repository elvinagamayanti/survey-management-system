/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.service.impl;

import com.example.sms.dto.SatkerDto;
import com.example.sms.entity.Satker;
import com.example.sms.mapper.SatkerMapper;
import com.example.sms.repository.SatkerRepository;
import com.example.sms.service.SatkerService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 *
 * @author pinaa
 */
@Service
public class SatkerServiceImpl implements SatkerService{
    private SatkerRepository satkerRepository;
    
    public SatkerServiceImpl(SatkerRepository satkerRepository) {
        this.satkerRepository = satkerRepository;
    }
    
    @Override
    public List<SatkerDto> ambilDaftarSatker() {
        List<Satker> satkers = this.satkerRepository.findAll();
        List<SatkerDto> satkerDtos = satkers.stream()
                .map((satker) -> (SatkerMapper.mapToSatkerDto(satker)))
                .collect(Collectors.toList());        
        return satkerDtos;
    }
    
    @Override
    public void hapusDataSatker(Long satkerId) {
        satkerRepository.deleteById(satkerId);        
    }
    
    @Override
    public void perbaruiDataSatker(SatkerDto satkerDto) {
        Satker satker = SatkerMapper.mapToSatker(satkerDto);
        System.out.println(satkerDto);
        satkerRepository.save(satker);
    }
    
    @Override
    public void simpanDataSatker(SatkerDto satkerDto) {
        Satker satker = SatkerMapper.mapToSatker(satkerDto);
        //System.out.println(student);
        satkerRepository.save(satker);
    }

    @Override
    public SatkerDto cariSatkerById(Long id) {
        Satker satker = satkerRepository.findById(id).get();
        return  SatkerMapper.mapToSatkerDto(satker);
    }
}
