package com.github.maciejmalewicz.Desert21;

import com.github.maciejmalewicz.Desert21.domain.accountManagement.AccountAcceptanceRequest;
import com.github.maciejmalewicz.Desert21.repository.AccountAcceptanceRequestRepository;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class Desert21ApplicationTests {

    @Value("${spring.addresses.baseUrl}")
    private String address;

    @Autowired
    private AccountAcceptanceRequestRepository repository;

    @Test
    public void isAbleToConnectToDb() {
        repository.save(new AccountAcceptanceRequest(
                "Maciek@gmaail.com",
                "macior",
                "mienso12",
                "ABCABC"
        ));
        var requests = repository.findAll();
        assertEquals(requests.size(), 1);
    }

}
