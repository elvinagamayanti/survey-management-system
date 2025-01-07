/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.dto;

import com.example.sms.entity.Role;
import com.example.sms.entity.Satker;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author pinaa
 */

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String nip;
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    private String password;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="satker_id")
    private Satker satker;
    
    public String getNamaSatker(){
        return "Badan Pusat Statistik " + satker.getName();
    }
    
    public String getFullName(){
        return firstName + " " + lastName;
    }
}
