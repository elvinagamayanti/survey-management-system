package com.example.sms.entity.integration;

import com.example.sms.entity.Kegiatan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "integration_tracking",
       uniqueConstraints = @UniqueConstraint(columnNames = {"kegiatan_id", "external_system"}))
public class IntegrationTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kegiatan_id", nullable = false)
    private Kegiatan kegiatan;
    
    @Column(name = "external_system", nullable = false)
    private String externalSystem; // 'IPLAN', 'FASIH_QD', 'FASIH_SM'
    
    @Column(name = "external_id")
    private String externalId;
    
    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;
    
    @Column(name = "sync_status")
    private String syncStatus; // 'SUCCESS', 'FAILED', 'IN_PROGRESS'
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "sync_count")
    private Integer syncCount = 0;
    
    @Column(name = "data_hash")
    private String dataHash;
    
    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (lastSyncTime == null) {
            lastSyncTime = LocalDateTime.now();
        }
    }
}