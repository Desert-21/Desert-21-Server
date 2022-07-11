package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import lombok.Getter;

@Getter
public enum ActionType {
    LAB_EVENT(LabAction.class),
    BUILD(BuildAction.class),
    UPGRADE(UpgradeAction.class),
    TRAIN(TrainAction.class),
    MOVE_UNITS(MoveUnitsAction.class),
    ATTACK(AttackAction.class),
    FIRE_ROCKET(FireRocketAction.class),
    BOMBARD(BombardAction.class);

    private final Class<? extends Action> actionClass;

    ActionType(Class<? extends Action> action) {
        this.actionClass = action;
    }
}
