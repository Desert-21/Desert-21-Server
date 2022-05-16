package com.github.maciejmalewicz.Desert21.dto.balance;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.CombatBranchConfig;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.ControlBranchConfig;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.ProductionBranchConfig;

public record AllUpgradesBalanceDto(CombatBranchConfig combat,
                                    ControlBranchConfig control,
                                    ProductionBranchConfig production) {
}
