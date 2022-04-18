package com.github.maciejmalewicz.Desert21.service.gameSnapshot;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.dto.game.BuildingDto;
import com.github.maciejmalewicz.Desert21.dto.game.FieldDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GamePlayerService;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class GameSnapshotServiceTest {

    @Autowired
    private GameRepository gameRepository;

    private GameSnapshotService tested;

    @Autowired
    private GamePlayerService gamePlayerService;

    private final Army army = new Army(10, 10, 10);

    private Game game;

    private Authentication authentication;

    @BeforeEach
    void setup() {
        var armySnapshotProcessingService = mock(ArmySnapshotProcessingService.class);
        doReturn(army).when(armySnapshotProcessingService).snapshotArmy(
                any(), any(), any()
        );
        tested = new GameSnapshotService(gamePlayerService, armySnapshotProcessingService);

        setupGame();
        setupAuthentication();
    }

    void setupGame() {
        game = new Game(
                List.of(
                        new Player("AA",
                                "macior123456",
                                new ResourceSet(60, 60, 60)),
                        new Player("BB",
                                "schabina123456",
                                new ResourceSet(60, 60, 60))),
                generateFields(),
                new StateManager(
                        GameState.WAITING_TO_START,
                        DateUtils.millisecondsFromNow(10_000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game = gameRepository.save(game);
    }

    void setupAuthentication() {
        var authorities = List.of(
                new SimpleGrantedAuthority("USER_AA"),
                new SimpleGrantedAuthority("Any random authority")
        );
        authentication = mock(Authentication.class);
        doReturn(authorities).when(authentication).getAuthorities();
    }

    Field[][] generateFields() {
        var fields = new Field[3][3];
        IntStream.range(0, fields.length).forEach(i -> {
            fields[i] = new Field[3];
        });

        IntStream.range(0, fields.length).forEach(i -> {
            IntStream.range(0, fields[i].length).forEach(j -> {
                fields[i][j] = new Field(new Building(BuildingType.EMPTY_FIELD));
            });
        });

        fields[0][0].getBuilding().setType(BuildingType.HOME_BASE);
        fields[0][1].getBuilding().setType(BuildingType.TOWER);
        fields[0][2].getBuilding().setType(BuildingType.ROCKET_LAUNCHER);
        fields[1][0].getBuilding().setType(BuildingType.METAL_FACTORY);
        fields[1][1].getBuilding().setType(BuildingType.BUILDING_MATERIALS_FACTORY);
        fields[1][2].getBuilding().setType(BuildingType.ELECTRICITY_FACTORY);

        fields[1][1].getBuilding().setLevel(2);
        fields[1][2].getBuilding().setLevel(3);

        fields[0][0].setOwnerId("AA");
        fields[2][2].setOwnerId("BB");
        return fields;
    }

    FieldDto fieldWith(BuildingType buildingType, int level, String ownerId) {
        return new FieldDto(
                new BuildingDto(buildingType, level),
                ownerId,
                new Army(10, 10, 10)
        );
    }

    @Test
    void snapshotGame() throws NotAcceptableException {
        var gameSnapshot = tested.snapshotGame(game.getId(), authentication);
        assertEquals(game.getId(), gameSnapshot.gameId());

        assertEquals(2, gameSnapshot.players().size());
        var player1 = gameSnapshot.players().get(0);
        var player2 = gameSnapshot.players().get(1);
        assertEquals("AA", player1.id());
        assertEquals("macior123456", player1.nickname());
        assertEquals(new ResourceSet(60, 60, 60), player1.resources());
        assertEquals("BB", player2.id());
        assertEquals("schabina123456", player2.nickname());
        assertEquals(new ResourceSet(60, 60, 60), player2.resources());

        var stateManager = gameSnapshot.stateManager();
        assertEquals(GameState.WAITING_TO_START, stateManager.gameState());
        assertEquals(game.getStateManager().getTimeout(), stateManager.timeout());
        assertEquals("AA", stateManager.currentPlayerId());

        var fields = gameSnapshot.fields();
        assertEquals(fieldWith(BuildingType.HOME_BASE, 1, "AA"), fields[0][0]);
        assertEquals(fieldWith(BuildingType.TOWER, 1, null), fields[0][1]);
        assertEquals(fieldWith(BuildingType.ROCKET_LAUNCHER, 1, null), fields[0][2]);

        assertEquals(fieldWith(BuildingType.METAL_FACTORY, 1, null), fields[1][0]);
        assertEquals(fieldWith(BuildingType.BUILDING_MATERIALS_FACTORY, 2, null), fields[1][1]);
        assertEquals(fieldWith(BuildingType.ELECTRICITY_FACTORY, 3, null), fields[1][2]);

        assertEquals(fieldWith(BuildingType.EMPTY_FIELD, 1, null), fields[2][0]);
        assertEquals(fieldWith(BuildingType.EMPTY_FIELD, 1, null), fields[2][1]);
        assertEquals(fieldWith(BuildingType.EMPTY_FIELD, 1, "BB"), fields[2][2]);
    }

    @Test
    void snapshotGameWhenGetGamePlayerFails() throws NotAcceptableException {
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.snapshotGame(game.getId(), authentication);
        });
        assertEquals("User could not be identified!", exception.getMessage());
    }
}