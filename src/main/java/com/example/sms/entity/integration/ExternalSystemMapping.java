package com.example.sms.entity.integration;

import com.example.sms.entity.Kegiatan;
import com.example.sms.entity.Tahap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "external_system_mapping",
       uniqueConstraints = @UniqueConstraint(columnNames = {"kegiatan_id", "tahap"}))
public class ExternalSystemMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kegiatan_id", nullable = false)
    private Kegiatan kegiatan;
    
    @Column(name = "tahap", nullable = false)
    @Enumerated(EnumType.STRING)
    private Tahap tahap;
    
    @Column(name = "external_system", nullable = false)
    private String externalSystem; // 'IPLAN', 'FASIH_QD', 'FASIH_SM'
    
    @Column(name = "external_endpoint")
    private String externalEndpoint;
    
    @Column(name = "api_key")
    private String apiKey;
    
    @Column(name = "external_id")
    private String externalId;
    
    @Column(name = "enabled")
    private Boolean enabled = true;
}