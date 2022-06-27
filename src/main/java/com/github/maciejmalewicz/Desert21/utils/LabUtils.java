package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabBranchConfig;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.google.common.base.CaseFormat;

import java.util.List;

public class LabUtils {

    public static int getUpgradeCost(LabUpgrade labUpgrade, GameBalanceDto balanceDto) throws NotAcceptableException {
        var labBranchConfig = getLabBranchConfig(labUpgrade, balanceDto);
        var costConfig = labBranchConfig.getCostConfig();
        var costConfigClass = costConfig.getClass();
        var objectFieldName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, labUpgrade.name());
        try {
            var objectField = costConfigClass.getDeclaredField(objectFieldName);
            objectField.setAccessible(true);
            return (Integer) objectField.get(costConfig);
        } catch (Exception e) {
            throw new NotAcceptableException("Could not reach the lab upgrade cost!");
        }
    }

    public static LabBranchConfig getLabBranchConfig(LabUpgrade labUpgrade, GameBalanceDto balanceDto) throws NotAcceptableException {
        var upgradesConfig = balanceDto.upgrades();
        var branches = List.of(
                upgradesConfig.combat(),
                upgradesConfig.control(),
                upgradesConfig.production()
        );
        return branches.stream()
                .filter(b -> b.containsUpgrade(labUpgrade))
                .findFirst()
                .orElseThrow(() -> new NotAcceptableException("Lab branch not found!"));
    }
}
