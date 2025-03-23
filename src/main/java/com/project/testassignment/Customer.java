package com.project.testassignment;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Этот класс представляет абонента в сервисе.
 * Он содержит ID абонента, а также его номер.
 */
@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    private String number;
}
