package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleUpgradePerLocationValidatable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SingleUpgradePerLocationValidatorTest {

    @Autowired
    private SingleUpgradePerLocationValidator tested;

    @Test
    void validateHappyPath() {
        var validatables = List.of(
                new SingleUpgradePerLocationValidatable(new Location(0, 0)),
                new SingleUpgradePerLocationValidatable(new Location(0, 1)),
                new SingleUpgradePerLocationValidatable(new Location(2, 0)),
                new SingleUpgradePerLocationValidatable(new Location(3, 0))
        );
        assertTrue(tested.validate(validatables, null));
    }

    @Test
    void validateOneLocationRepeated() {
        var validatables = List.of(
                new SingleUpgradePerLocationValidatable(new Location(0, 0)),
                new SingleUpgradePerLocationValidatable(new Location(0, 1)),
                new SingleUpgradePerLocationValidatable(new Location(2, 0)),
                new SingleUpgradePerLocationValidatable(new Location(3, 0)),
                new SingleUpgradePerLocationValidatable(new Location(3, 0))
        );
        assertFalse(tested.validate(validatables, null));
    }
}