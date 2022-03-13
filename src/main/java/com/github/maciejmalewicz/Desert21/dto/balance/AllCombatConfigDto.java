package com.github.maciejmalewicz.Desert21.dto.balance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.maciejmalewicz.Desert21.misc.balance.CombatUnitConfig;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AllCombatConfigDto(
        CombatUnitConfig droids,
        CombatUnitConfig tanks,
        CombatUnitConfig cannons
) {
}
