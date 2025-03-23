package com.project.testassignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA репозиторий для класса Customer
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    public Customer findByNumber(String number);
}
