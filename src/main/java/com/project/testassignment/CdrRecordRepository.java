package com.project.testassignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Month;
import java.util.List;

public interface CdrRecordRepository extends JpaRepository<CdrRecord, Long> {
    @Query("select r from CdrRecord r where function('MONTH', r.callStartDateTime) = :month")
    List<CdrRecord> findAllByCallStartDateTimeMonth(@Param("month") int month);
    @Query("select r from CdrRecord r where function('MONTH', r.callStartDateTime) = :month and (r.callerNumber = :customerNumber or r.calleeNumber = :customerNumber)")
    List<CdrRecord> findAllByCallStartDateTimeMonthAndCustomer(@Param("month") int month, @Param("customerNumber") String customerNumber);
    @Query("select r from CdrRecord r where (r.callerNumber = :customerNumber or r.calleeNumber = :customerNumber)")
    List<CdrRecord> findAllByCustomer(@Param("customerNumber") String customerNumber);
}
