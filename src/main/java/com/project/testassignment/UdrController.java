package com.project.testassignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Контроллер, содержащий эндпоинты REST API.
 * @author Martyshov Danila
 */
@Controller
@RequestMapping("/api/udr")
@CrossOrigin
public class UdrController {
    private CdrGeneratorService cdrGeneratorService;
    private CustomerRepository customerRepository;
    private CdrRecordRepository cdrRecordRepository;
    @Autowired
    public UdrController(CdrGeneratorService cdrGeneratorService, CustomerRepository customerRepository, CdrRecordRepository cdrRecordRepository){
        this.cdrGeneratorService = cdrGeneratorService;
        this.customerRepository = customerRepository;
        this.cdrRecordRepository = cdrRecordRepository;
    }
    /**
     * Этот метод по запросу post генерирует новые CDR записи в базу данных
     */
    @PostMapping("/generate")
    public void generateCdr(){
        cdrGeneratorService.generate();
    }

    /**
     * Этот метод является реализацией первого условия второго задания. Возвращает UDR по указанному абоненту за срок, указанный в параметре - либо за месяц (параметр равен от 1 до 12), либо за все время обслуживания.
     * @param customerId - целочисленный ID пользователя
     * @param month - параметр запроса - месяц от 1 до 12 или другое целочисленное значение/null.
     * @return UDR запись
     */
    @GetMapping("/getUdrByCustomer/{customerId}")
    public ResponseEntity<UdrRecord> getUdrByCustomer(@PathVariable("customerId") Long customerId, @Param("month") Integer month){
        var customer = customerRepository.findById(customerId).get();
        List<CdrRecord> cdrList;
        if (month==null || !(month <= 12 && month >= 1)){
            cdrList = cdrRecordRepository.findAllByCustomer(customer.getNumber());
        } else {
            cdrList = cdrRecordRepository.findAllByCallStartDateTimeMonthAndCustomer(month, customer.getNumber());
        }
        var udr = new UdrRecord();
        udr.setIncomingCallTotalTime(Duration.ZERO);
        udr.setOutcomingCallTotalTime(Duration.ZERO);
        udr.setNumber(customer.getNumber());
        for (var cdr: cdrList){
            if (cdr.getCallType().equals("01")){
                if (cdr.getCallerNumber().equals(customer.getNumber())){
                    udr.setOutcomingCallTotalTime(udr.getOutcomingCallTotalTime().plus(Duration.between(cdr.getCallStartDateTime(), cdr.getCallEndDateTime())));
                    udr.setIncomingCallTotalTime(udr.getIncomingCallTotalTime());
                } else {
                    udr.setIncomingCallTotalTime(udr.getIncomingCallTotalTime().plus(Duration.between(cdr.getCallStartDateTime(), cdr.getCallEndDateTime())));
                    udr.setOutcomingCallTotalTime(udr.getOutcomingCallTotalTime());
                }
            } else {
                if (cdr.getCallerNumber().equals(customer.getNumber())){
                    udr.setIncomingCallTotalTime(udr.getIncomingCallTotalTime());
                    udr.setOutcomingCallTotalTime(udr.getOutcomingCallTotalTime().plus(Duration.between(cdr.getCallStartDateTime(), cdr.getCallEndDateTime())));

                } else {
                    udr.setIncomingCallTotalTime(udr.getIncomingCallTotalTime().plus(Duration.between(cdr.getCallStartDateTime(), cdr.getCallEndDateTime())));
                    udr.setOutcomingCallTotalTime(udr.getOutcomingCallTotalTime());
                }
            }
        }
        return ResponseEntity.ok(udr);
    }

    /**
     * Этот метод является реализацией второго условия второго задания. Возвращает UDR по всем абонентам за указанный месяц.
     * @param month - параметр запроса - месяц от 1 до 12.
     * @return Список UDR записей
     */
    @GetMapping("/getUdrsByMonth/{month}")
    public ResponseEntity<List<UdrRecord>> getUdrsByMonth(@PathVariable("month") Integer month){
        if (month==null || !(month <= 12 && month >= 1)){
            return ResponseEntity.badRequest().body(null);
        }
        var cdrList = cdrRecordRepository.findAllByCallStartDateTimeMonth(month);
        var udrMap = new HashMap<String, UdrRecord>();
        for (var cdr: cdrList){
            if (cdr.getCallType().equals("01")){
                initializeKeysIfAbscent(udrMap, cdr);
                var newRec = new UdrRecord();
                newRec.setIncomingCallTotalTime(udrMap.get(cdr.getCalleeNumber()).getIncomingCallTotalTime().plus(Duration.between(cdr.getCallStartDateTime(), cdr.getCallEndDateTime())));
                newRec.setOutcomingCallTotalTime(udrMap.get(cdr.getCalleeNumber()).getOutcomingCallTotalTime());
                newRec.setNumber(cdr.getCalleeNumber());
                udrMap.put(cdr.getCalleeNumber(), newRec);
                var calleeRec = new UdrRecord();
                calleeRec.setOutcomingCallTotalTime(udrMap.get(cdr.getCallerNumber()).getOutcomingCallTotalTime().plus(Duration.between(cdr.getCallStartDateTime(), cdr.getCallEndDateTime())));
                calleeRec.setIncomingCallTotalTime(udrMap.get(cdr.getCallerNumber()).getIncomingCallTotalTime());
                calleeRec.setNumber(cdr.getCallerNumber());
                udrMap.put(cdr.getCallerNumber(), calleeRec);
            } else {
                initializeKeysIfAbscent(udrMap, cdr);
                var newRec = new UdrRecord();
                newRec.setIncomingCallTotalTime(udrMap.get(cdr.getCallerNumber()).getIncomingCallTotalTime());
                newRec.setOutcomingCallTotalTime(udrMap.get(cdr.getCallerNumber()).getOutcomingCallTotalTime().plus(Duration.between(cdr.getCallStartDateTime(), cdr.getCallEndDateTime())));
                newRec.setNumber(cdr.getCallerNumber());
                udrMap.put(cdr.getCallerNumber(), newRec);
                var calleeRec = new UdrRecord();
                calleeRec.setIncomingCallTotalTime(udrMap.get(cdr.getCalleeNumber()).getIncomingCallTotalTime().plus(Duration.between(cdr.getCallStartDateTime(), cdr.getCallEndDateTime())));
                calleeRec.setOutcomingCallTotalTime(udrMap.get(cdr.getCalleeNumber()).getOutcomingCallTotalTime());
                calleeRec.setNumber(cdr.getCalleeNumber());
                udrMap.put(cdr.getCalleeNumber(), calleeRec);
            }
        }
        return ResponseEntity.ok(new ArrayList<>(udrMap.values()));
    }
    private void initializeKeysIfAbscent(HashMap<String, UdrRecord> udrMap, CdrRecord cdr){
        if (!udrMap.containsKey(cdr.getCallerNumber())){
            var record = new UdrRecord();
            record.setNumber(cdr.getCallerNumber());
            record.setIncomingCallTotalTime(Duration.ZERO);
            record.setOutcomingCallTotalTime(Duration.ZERO);
            udrMap.put(cdr.getCallerNumber(), record);
        }
        if (!udrMap.containsKey(cdr.getCalleeNumber())){
            var record = new UdrRecord();
            record.setNumber(cdr.getCalleeNumber());
            record.setIncomingCallTotalTime(Duration.ZERO);
            record.setOutcomingCallTotalTime(Duration.ZERO);
            udrMap.put(cdr.getCalleeNumber(), record);
        }
    }
}
