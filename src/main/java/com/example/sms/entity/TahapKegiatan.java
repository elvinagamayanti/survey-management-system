package com.example.sms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tahap_kegiatan")
public class TahapKegiatan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kegiatan_id", nullable = false)
    private Kegiatan kegiatan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tahap tahap;  // Using the existing Tahap enum

    @Column(nullable = false)
    private Boolean isCompleted = false;
    
    @Column(nullable = false)
    private Boolean isActive = false;
    
    @Column(nullable = true)
    private Integer progressPercentage = 0;
    
    // We'll store the sub-steps as JSON in the database
    @Column(columnDefinition = "TEXT")
    private String subSteps;
    
    @Column(nullable = true)
    private String uploadedFiles;
    
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdOn;
    
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date updatedOn;
}