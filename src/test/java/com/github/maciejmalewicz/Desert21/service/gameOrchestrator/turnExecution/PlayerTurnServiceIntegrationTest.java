package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersActionDto;
import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersTurnDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.GamePlayerData;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.GamePlayerService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.TurnResolutionPhaseStartService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.ActionType;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.UpgradeAction;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//testing single players action one by one (happy paths)
@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class PlayerTurnServiceIntegrationTest {

    @Autowired
    private ApplicationUserRepository playerRepository;

    private GamePlayerService gamePlayerService;

    @Autowired
    private GameBalanceService gameBalanceService;

    @Autowired
    private PlayersActionsValidatingService validatingService;

    @Autowired
    private GameEventsExecutionService eventsExecutionService;

    @Autowired
    private GameRepository gameRepository;

    private TurnResolutionPhaseStartService turnResolutionPhaseStartService;

    private PlayerTurnService tested;

    private Game game;

    private Player player;

    void setupGameAndPlayer() {
        player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        game = new Game(
                List.of(
                        player,
                        new Player("BB",
                                "schabina123456",
                                new ResourceSet(60, 60, 60))),
                new Field[9][9],
                new StateManager(
                        GameState.AWAITING,
                        DateUtils.millisecondsFromNow(10_000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game.getFields()[0][0] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY, 1), "AA");
        gameRepository.save(game);
    }

    void setupTested() throws NotAcceptableException {
        gamePlayerService = mock(GamePlayerService.class);
        doReturn(new GamePlayerData(game, player)).when(gamePlayerService).getGamePlayerData(anyString(), any());

        turnResolutionPhaseStartService = mock(TurnResolutionPhaseStartService.class);
        tested = new PlayerTurnService(
                gamePlayerService,
                gameBalanceService,
                validatingService,
                eventsExecutionService,
                gameRepository,
                turnResolutionPhaseStartService
        );
    }

    @BeforeEach
    void setup() throws NotAcceptableException {
        setupGameAndPlayer();
        setupTested();
    }

    @Test
    void integrationUpgradeBuilding() throws NotAcceptableException {
        var upgradeContent = new UpgradeAction(new Location(0, 0));
        var map = new ObjectMapper().convertValue(upgradeContent, LinkedHashMap.class);
        var dto = new PlayersTurnDto("IGNORED", List.of(
                new PlayersActionDto(ActionType.UPGRADE, map))
        );
        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();

        assertEquals(new ResourceSet(60, 20, 60), savedGame.getCurrentPlayer().get().getResources());
        assertEquals(new Building(BuildingType.ELECTRICITY_FACTORY, 2), savedGame.getFields()[0][0].getBuilding());

        verify(turnResolutionPhaseStartService, times(1)).stateTransition(savedGame);
    }

}