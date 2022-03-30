package com.github.maciejmalewicz.Desert21;

import com.github.maciejmalewicz.Desert21.domain.accountManagement.AccountAcceptanceRequest;
import com.github.maciejmalewicz.Desert21.repository.AccountAcceptanceRequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class Desert21ApplicationTests {

    @Value("${spring.addresses.baseUrl}")
    private String address;

    @Autowired
    private AccountAcceptanceRequestRepository repository;

    @Test
    public void isAbletoConnectToDb() {
        repository.save(new AccountAcceptanceRequest(
                "Maciek@gmaail.com",
                "macior",
                "mienso12",
                "ABCABC"
        ));
        var requests = repository.findAll();
        assertEquals(requests.size(), 1);
        assertEquals(2, 1);
    }

    @Test
    void contextLoads() {
        System.out.println(address);
    }

}
