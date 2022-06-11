package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.dto.balance.AllCombatBalanceDto;
import com.github.maciejmalewicz.Desert21.models.balance.CombatUnitConfig;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.PathLengthValidatable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PathLengthValidator implements ActionValidator<PathLengthValidatable> {

    @Override
    public boolean validate(List<PathLengthValidatable> validatables, TurnExecutionContext context) {
        var combatConfig = context.gameBalance().combat();
        return validatables.stream().allMatch(v -> validateSingle(v, combatConfig));
    }

    private boolean validateSingle(PathLengthValidatable validatable, AllCombatBalanceDto combatBalance) {
        var maxDistance = mapMaxDistanceTraveled(validatable.army(), combatBalance);
        var distanceToTravel = validatable.path().size() - 1;
        return distanceToTravel <= maxDistance;
    }

    private int mapMaxDistanceTraveled(Army army, AllCombatBalanceDto combatBalance) {
        var optionalDroidsConfig = army.getDroids() > 0 ?
                Optional.of(combatBalance.droids()) :
                Optional.empty();
        var optionalTanksConfig = army.getTanks() > 0 ?
                Optional.of(combatBalance.tanks()) :
                Optional.empty();
        var optionalCannonsConfig = army.getCannons() > 0 ?
                Optional.of(combatBalance.cannons()) :
                Optional.empty();
        return Stream.of(optionalDroidsConfig, optionalTanksConfig, optionalCannonsConfig)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(CombatUnitConfig.class::cast)
                .map(CombatUnitConfig::getFieldsTraveledPerTurn)
                .min(Comparator.comparingInt(a -> a))
                .orElse(0);
    }
}
