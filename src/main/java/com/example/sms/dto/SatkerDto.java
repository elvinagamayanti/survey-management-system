/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author pinaa
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SatkerDto {
    private Long id;
    @NotEmpty(message = "Nama Satuan Kerja tidak boleh kosong")
    private String name;
    @NotEmpty(message = "Kode Satuan Kerja tidak boleh kosong")
    private String code;
    private String address;
    private String number;
    private String email;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdOn;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date updatedOn; 
}

