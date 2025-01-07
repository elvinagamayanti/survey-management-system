/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdOn;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date updatedOn; 
}

