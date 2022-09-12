package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.dto.orchestrator.PlayersTurnDto;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.GamePlayerData;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.GamePlayerService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.TurnResolutionPhaseStartService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class PlayerTurnServiceTest {

    private record MockResult(String prop) implements EventResult {
        @Override
        public long millisecondsToView() {
            return 1000;
        }
    };

    private GameRepository gameRepository;

    private PlayerTurnService tested;

    @Autowired
    private GameBalanceService gameBalanceService;

    private PlayersActionsValidatingService validatingService;

    private TurnResolutionPhaseStartService turnResolutionPhaseStartService;

    private GameEventsExecutionService eventsExecutionService;

    private GamePlayerService gamePlayerService;

    private Authentication authentication;

    private Game game;

    private Player player;

    private List<EventResult> eventResults;

    void setupAuth() {
        var authorities = List.of(
                new SimpleGrantedAuthority("USER_AA"),
                new SimpleGrantedAuthority("Any random authority")
        );
        authentication = mock(Authentication.class);
        doReturn(authorities).when(authentication).getAuthorities();
    }

    void setupTested() throws NotAcceptableException, AuthorizationException {
        gamePlayerService = mock(GamePlayerService.class);
        doReturn(new GamePlayerData(game, player)).when(gamePlayerService).getGamePlayerData(anyString(), any());

        validatingService = mock(PlayersActionsValidatingService.class);
        doReturn(true).when(validatingService).validatePlayersActions(any(), any());

        eventResults = List.of(new MockResult("AA"), new MockResult("BB")); //todo add checking assertions in tests
        eventsExecutionService = mock(GameEventsExecutionService.class);
        var answer = new Answer<EventExecutionResult>() {
            public EventExecutionResult answer(InvocationOnMock invocation) {
                //applying random change
                var context = invocation.getArgument(1, TurnExecutionContext.class);
                context.game().getFields()[0][0] = new Field(new Building(BuildingType.TOWER));
                return new EventExecutionResult(context, eventResults);
            }
        };
        doAnswer(answer)
                .when(eventsExecutionService)
                .executeEvents(any(), any());
        turnResolutionPhaseStartService = mock(TurnResolutionPhaseStartService.class);
        gameRepository = mock(GameRepository.class);
        var gameRepoAnswer = new Answer<Game>() {
            public Game answer(InvocationOnMock invocation) {
                return invocation.getArgument(0, Game.class);
            }
        };
        doAnswer(gameRepoAnswer).when(gameRepository).save(any(Game.class));
        tested = new PlayerTurnService(
                gamePlayerService,
                gameBalanceService,
                validatingService,
                eventsExecutionService,
                gameRepository,
                turnResolutionPhaseStartService);
    }

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
    }

    @BeforeEach
    void setup() throws NotAcceptableException, AuthorizationException  {
        setupGameAndPlayer();
        setupAuth();
        setupTested();
    }

    @Test
    void executeTurnHappyPath() throws NotAcceptableException, AuthorizationException  {
        var dto = new PlayersTurnDto(
                "IGNORED",
                new ArrayList<>()
        );
        tested.executeTurn(authentication, dto);

        var gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        verify(turnResolutionPhaseStartService, times(1)).stateTransition(gameArgumentCaptor.capture());
        var savedGame = gameArgumentCaptor.getAllValues().get(0);
        assertEquals(new Field(new Building(BuildingType.TOWER)), savedGame.getFields()[0][0]);
        assertEquals(eventResults, savedGame.getCurrentEventResults());
    }

    @Test
    void executeTurnWrongGameState() {
        game.getStateManager().setGameState(GameState.RESOLVED);
        var dto = new PlayersTurnDto(
                "IGNORED",
                new ArrayList<>()
        );
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.executeTurn(authentication, dto);
        });
        assertEquals("Cannot execute turn now!", exception.getMessage());
    }

    @Test
    void executeTurnWrongPlayer() {
        game.getStateManager().setCurrentPlayerId("BB");
        var dto = new PlayersTurnDto(
                "IGNORED",
                new ArrayList<>()
        );
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.executeTurn(authentication, dto);
        });
        assertEquals("Cannot execute turn now!", exception.getMessage());
    }


    @Test
    void executeTurnGamePlayerFailing() throws NotAcceptableException, AuthorizationException  {
        doThrow(new NotAcceptableException("TEST ERROR")).when(gamePlayerService).getGamePlayerData(anyString(), any());
        var dto = new PlayersTurnDto(
                "IGNORED",
                new ArrayList<>()
        );
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.executeTurn(authentication, dto);
        });
        assertEquals("TEST ERROR", exception.getMessage());
        verify(gameRepository, never()).save(any());
        verify(turnResolutionPhaseStartService, never()).stateTransition(any());
    }

    @Test
    void executeTurnValidationFailing() {
        doReturn(false).when(validatingService).validatePlayersActions(any(), any());
        var dto = new PlayersTurnDto(
                "IGNORED",
                new ArrayList<>()
        );
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.executeTurn(authentication, dto);
        });
        assertEquals("Could not execute actions! Stop trying to hack this game, or you will get banned! -,-", exception.getMessage());
        verify(gameRepository, never()).save(any());
        verify(turnResolutionPhaseStartService, never()).stateTransition(any());
    }

    @Test
    void executeEventExecutionFailing() throws NotAcceptableException {
        doThrow(new NotAcceptableException("TEST ERROR")).when(eventsExecutionService).executeEvents(any(), any());
        var dto = new PlayersTurnDto(
                "IGNORED",
                new ArrayList<>()
        );
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.executeTurn(authentication, dto);
        });
        assertEquals("TEST ERROR", exception.getMessage());
        verify(gameRepository, never()).save(any());
        verify(turnResolutionPhaseStartService, never()).stateTransition(any());
    }
}