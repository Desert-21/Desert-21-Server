package com.github.maciejmalewicz.Desert21.dto.balance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.maciejmalewicz.Desert21.models.balance.CombatUnitConfig;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AllCombatBalanceDto(
        CombatUnitConfig droids,
        CombatUnitConfig tanks,
        CombatUnitConfig cannons
) {
}
