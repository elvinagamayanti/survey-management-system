/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.mapper;

import com.example.sms.dto.SatkerDto;
import com.example.sms.entity.Satker;

/**
 *
 * @author pinaa
 */
public class SatkerMapper {    

    public static SatkerDto mapToSatkerDto(Satker satker) {
        SatkerDto satkerDto = SatkerDto.builder()
                .id(satker.getId())
                .name(satker.getName())
                .code(satker.getCode())
                .address(satker.getAddress())
                .number(satker.getNumber())
                .email(satker.getEmail())
                .createdOn(satker.getCreatedOn())
                .updatedOn(satker.getUpdatedOn())
                .build();        
        return satkerDto;
    }    

    public static Satker mapToSatker(SatkerDto satkerDto) {
        Satker satker = Satker.builder()
                .id(satkerDto.getId())
                .name(satkerDto.getName())
                .code(satkerDto.getCode())
                .address(satkerDto.getAddress())
                .number(satkerDto.getNumber())
                .email(satkerDto.getEmail())
                .createdOn(satkerDto.getCreatedOn())
                .updatedOn(satkerDto.getUpdatedOn())
                .build();        
        return satker;
    }
}
