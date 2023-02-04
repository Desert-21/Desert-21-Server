package com.github.maciejmalewicz.Desert21.ai.actionsGetters;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;

import java.util.List;

public interface CategoryActionsGetter<CategoryCost> {
    /**
     *
     * @param game current state of the game with next turn of the player
     * @param player player controlled/owned by AI
     * @return
     */
    List<ActionPossibility<CategoryCost>> getActions(Game game, Player player, GameBalanceDto gameBalance);
}
