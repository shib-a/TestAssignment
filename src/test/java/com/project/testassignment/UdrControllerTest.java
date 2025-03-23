package com.project.testassignment;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UdrControllerTest {
    private MockMvc mockMvc;
    private CustomerRepository customerRepository;
    private CdrRecordRepository cdrRecordRepository;
    @Autowired
    public UdrControllerTest(CustomerRepository customerRepository, CdrRecordRepository cdrRecordRepository, MockMvc mockMvc) {
        this.customerRepository = customerRepository;
        this.cdrRecordRepository = cdrRecordRepository;
        this.mockMvc = mockMvc;
    }
    @Test
    @Transactional
    public void testGetUdrByCustomerAllPeriod() throws Exception{
        initializeDatabase();
        var c = customerRepository.findByNumber("71111111");
        var expectedIncomingTime = Duration.between(
                LocalDateTime.of(2025,3,10,16,33,20),
                        LocalDateTime.of(2025,3,10,22,33,20)
                ).plus(
                        Duration.between(LocalDateTime.of(2025,1,1,1,1,1),
                                LocalDateTime.of(2025,1,1,1,2,1))
        );
        var expectedOutcomingTime = Duration.between(
                LocalDateTime.of(2025,3,20,16,33,20),
                LocalDateTime.of(2025,3,20,22,33,20)
        ).plus(
                Duration.between(LocalDateTime.of(2025,2,25,1,33,20),
                        LocalDateTime.of(2025,2,25,2,0,0))
        );
        mockMvc.perform(get(String.format("/api/udr/getUdrByCustomer/%s", c.getId()))
                .param("month", String.valueOf(0)))
                .andExpect(status().isOk()).andExpect(content().string(String.format("{\"number\":\"71111111\",\"incomingCallTotalTime\":\"%s\",\"outcomingCallTotalTime\":\"%s\"}", expectedIncomingTime, expectedOutcomingTime)));
    }
    @Test
    @Transactional
    public void testGetUdrByCustomerInMarch() throws Exception{
        initializeDatabase();
        var c = customerRepository.findByNumber("71111111");
        var expectedIncomingTime = Duration.between(
                LocalDateTime.of(2025,3,10,16,33,20),
                LocalDateTime.of(2025,3,10,22,33,20)
        );
        var expectedOutcomingTime = Duration.between(
                LocalDateTime.of(2025,3,20,16,33,20),
                LocalDateTime.of(2025,3,20,22,33,20)
        );
        mockMvc.perform(get(String.format("/api/udr/getUdrByCustomer/%s", c.getId()))
                        .param("month", String.valueOf(3)))
                .andExpect(status().isOk()).andExpect(content().string(String.format("{\"number\":\"71111111\",\"incomingCallTotalTime\":\"%s\",\"outcomingCallTotalTime\":\"%s\"}", expectedIncomingTime, expectedOutcomingTime)));
    }
    @Test
    @Transactional
    public void testGetUdrsByMonthInMarch() throws Exception{
        initializeDatabase();
        var firstCustomer = "71111110";
        var secondCustomer = "71111111";

        var firstCustomerExpectedIncomingTime = Duration.between(
                LocalDateTime.of(2025,3,20,16,33,20),
                LocalDateTime.of(2025,3,20,22,33,20)
        );
        var firstCustomerExpectedOutcomingTime = Duration.between(
                LocalDateTime.of(2025,3,10,16,33,20),
                LocalDateTime.of(2025,3,10,22,33,20)
        );
        var secondCustomerExpectedIncomingTime = Duration.between(
                LocalDateTime.of(2025,3,10,16,33,20),
                LocalDateTime.of(2025,3,10,22,33,20)
        );
        var secondCustomerExpectedOutcomingTime = Duration.between(
                LocalDateTime.of(2025,3,20,16,33,20),
                LocalDateTime.of(2025,3,20,22,33,20)
        );


        mockMvc.perform(get(String.format("/api/udr/getUdrsByMonth/%s", 3)))
                .andExpect(status().isOk()).andExpect(content().string(String.format("[{\"number\":\"%s\",\"incomingCallTotalTime\":\"%s\",\"outcomingCallTotalTime\":\"%s\"},{\"number\":\"%s\",\"incomingCallTotalTime\":\"%s\",\"outcomingCallTotalTime\":\"%s\"}]", firstCustomer, firstCustomerExpectedIncomingTime, firstCustomerExpectedOutcomingTime, secondCustomer, secondCustomerExpectedIncomingTime, secondCustomerExpectedOutcomingTime)));
    }
    @Test
    @Transactional
    public void testGeneration() throws Exception {
        initializeDatabase();
        mockMvc.perform(post("/api/udr/generate"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void initializeDatabase(){
        for (int i = 1; i < 4; i++){
            var customer = new Customer();
            customer.setNumber("7111111" + i);
//            customer.setId((long) i);
            customerRepository.save(customer);
        }
        var cdrRecord1 = new CdrRecord();
        cdrRecord1.setCalleeNumber("71111111");
        cdrRecord1.setCallerNumber("71111110");
        cdrRecord1.setCallType("01");
        cdrRecord1.setCallStartDateTime(LocalDateTime.of(2025,3,10,16,33,20));
        cdrRecord1.setCallEndDateTime(LocalDateTime.of(2025,3,10,22,33,20));
        cdrRecordRepository.save(cdrRecord1);

        var cdrRecord2 = new CdrRecord();
        cdrRecord2.setCalleeNumber("71111110");
        cdrRecord2.setCallerNumber("71111111");
        cdrRecord2.setCallType("02");
        cdrRecord2.setCallStartDateTime(LocalDateTime.of(2025,3,20,16,33,20));
        cdrRecord2.setCallEndDateTime(LocalDateTime.of(2025,3,20,22,33,20));
        cdrRecordRepository.save(cdrRecord2);

        var cdrRecord3 = new CdrRecord();
        cdrRecord3.setCalleeNumber("71111112");
        cdrRecord3.setCallerNumber("71111110");
        cdrRecord3.setCallType("01");
        cdrRecord3.setCallStartDateTime(LocalDateTime.of(2025,4,10,16,33,20));
        cdrRecord3.setCallEndDateTime(LocalDateTime.of(2025,4,10,16,36,3));
        cdrRecordRepository.save(cdrRecord3);

        var cdrRecord4 = new CdrRecord();
        cdrRecord4.setCalleeNumber("71111112");
        cdrRecord4.setCallerNumber("71111111");
        cdrRecord4.setCallType("02");
        cdrRecord4.setCallStartDateTime(LocalDateTime.of(2025,2,25,1,33,20));
        cdrRecord4.setCallEndDateTime(LocalDateTime.of(2025,2,25,2,0,0));
        cdrRecordRepository.save(cdrRecord4);

        var cdrRecord5 = new CdrRecord();
        cdrRecord5.setCalleeNumber("71111111");
        cdrRecord5.setCallerNumber("71111112");
        cdrRecord5.setCallType("01");
        cdrRecord5.setCallStartDateTime(LocalDateTime.of(2025,1,1,1,1,1));
        cdrRecord5.setCallEndDateTime(LocalDateTime.of(2025,1,1,1,2,1));
        cdrRecordRepository.save(cdrRecord5);
    }
}
