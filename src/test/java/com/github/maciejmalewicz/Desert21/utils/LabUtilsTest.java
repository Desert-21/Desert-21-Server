package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.maciejmalewicz.Desert21.utils.LabUtils.getLabBranchConfig;
import static com.github.maciejmalewicz.Desert21.utils.LabUtils.getUpgradeCost;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LabUtilsTest {

    @Autowired
    private GameBalanceService gameBalanceService;


    @Test
    void getUpgradeCostForSomeBasicUpgrade() throws NotAcceptableException {
        var cost = getUpgradeCost(LabUpgrade.HOME_SWEET_HOME, gameBalanceService.getGameBalance());
        assertEquals(100, cost);
    }

    @Test
    void getUpgradeCostShouldNotFailForAllTheLabUpgrades() throws NotAcceptableException {
        var allValues = LabUpgrade.values();
        for (LabUpgrade labUpgrade: allValues) {
            var cost = getUpgradeCost(labUpgrade, gameBalanceService.getGameBalance());
            assertTrue(cost > 0);
        }
    }

    @Test
    void getLabBranchConfigHappyPath() throws NotAcceptableException {
        var gameBalance = gameBalanceService.getGameBalance();
        var fromCombat = LabUpgrade.IMPROVED_TANKS;
        var fromControl = LabUpgrade.FACTORY_TURRET;
        var fromProduction = LabUpgrade.MORE_ELECTRICITY;

        assertThat(gameBalance.upgrades().combat(), sameBeanAs(getLabBranchConfig(fromCombat, gameBalance)));
        assertThat(gameBalance.upgrades().control(), sameBeanAs(getLabBranchConfig(fromControl, gameBalance)));
        assertThat(gameBalance.upgrades().production(), sameBeanAs(getLabBranchConfig(fromProduction, gameBalance)));
    }
}