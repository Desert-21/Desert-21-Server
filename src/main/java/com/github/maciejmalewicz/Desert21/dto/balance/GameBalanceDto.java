package com.github.maciejmalewicz.Desert21.dto.balance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.maciejmalewicz.Desert21.models.balance.GeneralBalanceConfig;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record GameBalanceDto(
        AllBuildingsBalanceDto buildings,
        AllCombatBalanceDto combat,
        AllUpgradesBalanceDto upgrades,
        GeneralBalanceConfig general
) {
}
