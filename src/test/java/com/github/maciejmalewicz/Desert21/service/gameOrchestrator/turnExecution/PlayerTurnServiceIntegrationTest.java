package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
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
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.LabUpgradeExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.TrainingMode;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
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
        player = new Player("AA", "macior123456", new ResourceSet(60, 60, 60));
        game = new Game(List.of(player, new Player("BB", "schabina123456", new ResourceSet(60, 60, 60))), BoardUtils.generateEmptyPlain(9), new StateManager(GameState.AWAITING, DateUtils.millisecondsFromNow(10_000), "AA", "TIMEOUTID"));
        game.getFields()[0][0] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY, 1), "AA");
        game.getFields()[0][1] = new Field(new Building(BuildingType.HOME_BASE, 3), "AA");
        game.getFields()[1][1] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "BB");

        gameRepository.save(game);
    }

    void setupTested() throws NotAcceptableException {
        gamePlayerService = mock(GamePlayerService.class);
        doReturn(new GamePlayerData(game, player)).when(gamePlayerService).getGamePlayerData(anyString(), any());

        turnResolutionPhaseStartService = mock(TurnResolutionPhaseStartService.class);
        tested = new PlayerTurnService(gamePlayerService, gameBalanceService, validatingService, eventsExecutionService, gameRepository, turnResolutionPhaseStartService);
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
        var dto = new PlayersTurnDto("IGNORED", List.of(new PlayersActionDto(ActionType.UPGRADE, map)));
        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();

        assertEquals(new ResourceSet(82, 42, 107), savedGame.getCurrentPlayer().get().getResources());
        assertEquals(new Building(BuildingType.ELECTRICITY_FACTORY, 2), savedGame.getFields()[0][0].getBuilding());

        verify(turnResolutionPhaseStartService, times(1)).stateTransition(savedGame);
    }

    @Test
    void integrationTrainArmy() throws NotAcceptableException {
        var trainContent = new TrainAction(new Location(0, 1), UnitType.DROID, TrainingMode.SMALL_PRODUCTION);
        var map = new ObjectMapper().convertValue(trainContent, LinkedHashMap.class);
        var dto = new PlayersTurnDto("IGNORED", List.of(new PlayersActionDto(ActionType.TRAIN, map)));
        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();

        assertEquals(new ResourceSet(32, 82, 97), savedGame.getCurrentPlayer().get().getResources());
        assertEquals(new Army(10, 0, 0), savedGame.getFields()[0][1].getArmy());

        verify(turnResolutionPhaseStartService, times(1)).stateTransition(savedGame);
    }

    @Test
    void integrationMoveArmy() throws NotAcceptableException {
        game.getFields()[0][0].setArmy(new Army(20, 10, 15));
        game.getFields()[0][1].setArmy(new Army(5, 0, 0));
        var moveContent = new MoveUnitsAction(new Location(0, 0), new Location(0, 1), List.of(new Location(0, 0), new Location(0, 1)), new Army(10, 5, 5));
        var map = new ObjectMapper().convertValue(moveContent, LinkedHashMap.class);
        var dto = new PlayersTurnDto("IGNORED", List.of(new PlayersActionDto(ActionType.MOVE_UNITS, map)));

        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();
        var fromField = BoardUtils.fieldAtLocation(savedGame.getFields(), new Location(0, 0));
        var toField = BoardUtils.fieldAtLocation(savedGame.getFields(), new Location(0, 1));
        assertEquals(new Army(10, 5, 10), fromField.getArmy());
        assertEquals(new Army(15, 5, 5), toField.getArmy());
    }

    @Test
    void integrationAttack() throws NotAcceptableException {
        game.getFields()[0][1].setArmy(new Army(20, 10, 15));
        game.getFields()[1][1].setArmy(new Army(5, 2, 10));
        var attackContent = new AttackAction(new Location(0, 1), new Location(1, 1), List.of(new Location(0, 1), new Location(1, 1)), new Army(10, 5, 10));
        var map = new ObjectMapper().convertValue(attackContent, LinkedHashMap.class);
        var dto = new PlayersTurnDto("IGNORED", List.of(new PlayersActionDto(ActionType.ATTACK, map)));

        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();
        var fromField = BoardUtils.fieldAtLocation(savedGame.getFields(), new Location(0, 1));
        var toField = BoardUtils.fieldAtLocation(savedGame.getFields(), new Location(1, 1));
        assertEquals(new Army(10, 5, 5), fromField.getArmy());
        // after calculations...
        assertEquals(new Army(5, 2, 5), toField.getArmy());
        assertEquals("AA", fromField.getOwnerId());
        assertEquals("AA", toField.getOwnerId());

        verify(turnResolutionPhaseStartService, times(1)).stateTransition(savedGame);
    }

    @Test
    void integrationLabUpgrade() throws NotAcceptableException {
        player.getOwnedUpgrades().add(LabUpgrade.HOME_SWEET_HOME);
        player.setResources(new ResourceSet(10, 100, 300));

        var labUpgradeContent = new LabAction(LabUpgrade.SCARAB_SCANNERS);
        var map = new ObjectMapper().convertValue(labUpgradeContent, LinkedHashMap.class);
        var dto = new PlayersTurnDto("IGNORED", List.of(new PlayersActionDto(ActionType.LAB_EVENT, map)));

        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();
        var currentPlayer = savedGame.getCurrentPlayer().orElseThrow();
        var upgrades = currentPlayer.getOwnedUpgrades();
        assertEquals(2, upgrades.size());
        assertEquals(LabUpgrade.HOME_SWEET_HOME, upgrades.get(0));
        assertEquals(LabUpgrade.SCARAB_SCANNERS, upgrades.get(1));

        assertEquals(new ResourceSet(42, 132, 247), currentPlayer.getResources());
    }

    @Test
    void integrationRocketStrike() throws NotAcceptableException {
        player.setResources(new ResourceSet(10, 100, 400));
        game.getFields()[6][6] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        game.getFields()[5][5] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "BB");
        game.getFields()[5][5].setArmy(new Army(10, 10, 10));

        var rocketStrikeActionContent = new FireRocketAction(new Location(5, 5), false);
        var map = new ObjectMapper().convertValue(rocketStrikeActionContent, LinkedHashMap.class);
        var dto = new PlayersTurnDto("IGNORED", List.of(new PlayersActionDto(ActionType.FIRE_ROCKET, map)));

        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();
        var currentPlayer = savedGame.getCurrentPlayer().orElseThrow();
        assertThat(new Army(5, 5, 5), sameBeanAs(savedGame.getFields()[5][5].getArmy()));
        assertEquals(new ResourceSet(34, 124, 139), currentPlayer.getResources());
        assertEquals(1, currentPlayer.getRocketStrikesDone());
    }

    @Test
    void integrationBuildBuildingTurn1() throws NotAcceptableException {
        player.setResources(new ResourceSet(60, 800, 60));
        player.getOwnedUpgrades().add(LabUpgrade.FACTORY_BUILDERS);
        game.getFields()[7][7] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");

        var buildBuildingAction = new BuildAction(new Location(7, 7), BuildingType.METAL_FACTORY);
        var map = new ObjectMapper().convertValue(buildBuildingAction, LinkedHashMap.class);
        var dto = new PlayersTurnDto("IGNORED", List.of(new PlayersActionDto(ActionType.BUILD, map)));

        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();
        var eventQueue = savedGame.getEventQueue();
        var expectedEventQueue = List.of(new BuildBuildingEvent(1, new Location(7, 7), BuildingType.METAL_FACTORY));
        assertThat(eventQueue, sameBeanAs(expectedEventQueue));
    }

    @Test
    void integrationBuildBuildingTurn2() throws NotAcceptableException {
        game.getFields()[7][7] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        game.getEventQueue().add(new BuildBuildingEvent(0, new Location(7, 7), BuildingType.METAL_FACTORY));

        var dto = new PlayersTurnDto("IGNORED", new ArrayList<>());

        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();
        var eventQueue = savedGame.getEventQueue();
        var expectedEventQueue = new ArrayList<>();
        assertThat(eventQueue, sameBeanAs(expectedEventQueue));

        var newBuilding = savedGame.getFields()[7][7].getBuilding();
        assertEquals(1, newBuilding.getLevel());
        assertEquals(BuildingType.METAL_FACTORY, newBuilding.getType());
    }

    @Test
    void integrationBombarding() throws NotAcceptableException {
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_CANNONS);

        game.getFields()[7][7] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        game.getFields()[7][7].setArmy(new Army(10, 10, 60));
        game.getFields()[7][8] = new Field(new Building(BuildingType.EMPTY_FIELD), "BB");
        game.getFields()[7][8].setArmy(new Army(1, 1, 1));

        var bombardingAction = new BombardAction(
                new Location(7, 7),
                new Location(7, 8),
                List.of(new Location(7, 7), new Location(7, 8)),
                50
        );
        var map = new ObjectMapper().convertValue(bombardingAction, LinkedHashMap.class);
        var dto = new PlayersTurnDto("IGNORED", List.of(new PlayersActionDto(ActionType.BOMBARD, map)));

        tested.executeTurn(mock(Authentication.class), dto);

        var savedGame = gameRepository.findAll().stream().findFirst().orElseThrow();
        var targetField = savedGame.getFields()[7][8];
        assertEquals(new Army(0, 0, 0), targetField.getArmy());
    }
}