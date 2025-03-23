package com.project.testassignment;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * Этот класс представляет объект CDR записи.
 * Он содержит ID, тип звонка, номер звонящего, номер принимающего звонок, дату и время начала звонка, а также дату и время конца звонка.
 */
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
