package com.project.testassignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

@Component
public class CdrGeneratorService {
    private CustomerRepository customerRepository;
    private CdrRecordRepository cdrRecordRepository;
    @Autowired
    public CdrGeneratorService(CustomerRepository customerRepository, CdrRecordRepository cdrRecordRepository){
        this.customerRepository = customerRepository;
        this.cdrRecordRepository = cdrRecordRepository;
    }
    public void generate(){
        Random random = new Random();
        int recordAmount = random.nextInt(10,20);
        List<Customer> customerList = customerRepository.findAll();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime startingTime = currentTime.minusYears(1);
        for (int i = 0; i < recordAmount; i++){
            CdrRecord cdrRecord = new CdrRecord();
            var calleeIndex = random.nextInt(0,customerList.size());
            var callerIndex = random.nextInt(0,customerList.size());
            var timeDifference = random.nextLong(1,5000);
            while(callerIndex == calleeIndex){
                callerIndex = random.nextInt(0,customerList.size());
            }
            cdrRecord.setCallType("0" + random.nextInt(1,3));
            cdrRecord.setCalleeNumber(customerList.get(calleeIndex).getNumber());
            cdrRecord.setCallerNumber(customerList.get(callerIndex).getNumber());
            cdrRecord.setCallStartDateTime(startingTime);
            startingTime = startingTime.plusSeconds(timeDifference);
            cdrRecord.setCallEndDateTime(startingTime);
            cdrRecordRepository.save(cdrRecord);
        }
    }
}
