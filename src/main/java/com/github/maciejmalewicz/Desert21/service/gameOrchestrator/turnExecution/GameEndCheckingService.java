package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameEndCheckingService {

    /**
     *
     * @param game game
     * @return empty optional if nobody has won, a string of winning player otherwise
     */
    public Optional<String> checkIfGameHasEnded(Game game) {
        var allFields = BoardUtils.boardToFieldList(game.getFields());
        var homeBases = allFields.stream().filter(f -> f.getBuilding().getType() == BuildingType.HOME_BASE);
        var distinctOwnersId = homeBases.map(Field::getOwnerId).distinct().toList();
        if (distinctOwnersId.size() > 1) {
            return Optional.empty();
        }
        var winnersId = distinctOwnersId.stream().findFirst().orElseThrow();
        return Optional.of(winnersId);
    }
}
