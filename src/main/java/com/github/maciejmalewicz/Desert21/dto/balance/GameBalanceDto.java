package com.github.maciejmalewicz.Desert21.dto.balance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record GameBalanceDto(
        AllBuildingsBalanceDto buildings,
        AllCombatConfigDto combat
) {
}
