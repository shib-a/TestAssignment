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
        LocalDateTime startingTime = currentTime.minusYears(1).minusSeconds(recordAmount * random.nextLong(1,100000000));
        for (int i = 0; i < recordAmount; i++){
            CdrRecord cdrRecord = new CdrRecord();
            var calleeIndex = random.nextInt(0,customerList.size());
            var callerIndex = random.nextInt(0,customerList.size());
            var timeDifference = (currentTime.toEpochSecond(ZoneOffset.ofTotalSeconds(0)) - startingTime.toEpochSecond(ZoneOffset.ofTotalSeconds(0)))/(recordAmount-i) + random.nextLong(1,currentTime.toEpochSecond(null) - startingTime.toEpochSecond(null));
            while(callerIndex == calleeIndex){
                callerIndex = random.nextInt(0,customerList.size());
            }
            cdrRecord.setCallType("0" + random.nextInt(0,2));
            cdrRecord.setCalleeNumber(customerList.get(calleeIndex).getNumber());
            cdrRecord.setCallerNumber(customerList.get(callerIndex).getNumber());
            cdrRecord.setCallStartDateTime(startingTime);
            startingTime = startingTime.plusSeconds(timeDifference);
            cdrRecord.setCallEndDateTime(LocalDateTime.ofEpochSecond(timeDifference, 0, ZoneOffset.ofTotalSeconds(0)));
            cdrRecordRepository.save(cdrRecord);
        }
    }
}
