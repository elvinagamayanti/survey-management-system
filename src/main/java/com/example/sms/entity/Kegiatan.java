package com.example.sms.entity;

import java.math.BigDecimal;
import java.time.Year;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "kegiatans")
public class Kegiatan {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = true)
    private String code;
    
    @Column(nullable = true)
    private BigDecimal budget;
    
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDate;
    
    @Column(nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Relasi ke User
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satker_id", nullable = false) // Relasi ke Satker
    private Satker satker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false) // Relasi ke Program
    private Program program;
    
    @Column(nullable = false)
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdOn;
    
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date updatedOn;    
    
    @Override
    public String toString(){
        return name;
    }

    public String getNamaSatker(){
        return "Badan Pusat Statistik " + satker.getName();
    }

    public String getNamaUser(){
        return user.getName();
    }

    public String getNamaProgram(){
        return program.getName();
    }

    public String getYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }
}

