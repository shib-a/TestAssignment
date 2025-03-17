package com.project.testassignment;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Data
public class CdrRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    private String callType;
    @Column(nullable = false)
    private String callerNumber;
    @Column(nullable = false)
    private String calleeNumber;
    @Column(nullable = false)
    private LocalDateTime callStartDateTime;
    @Column(nullable = false)
    private LocalDateTime callEndDateTime;
}
