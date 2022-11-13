package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.dto.RankingEntry;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class RankingServiceTest {

    private RankingService tested;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @BeforeEach
    void setup() {
        tested = new RankingService(applicationUserRepository);
    }

    @Test
    void shiftPlayersRankingsAfterGameFinishedFirstWins() {
        var user1 = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password"), 300));
        var user2 = applicationUserRepository.save(new ApplicationUser("schabina", new LoginData("schabina@gmail.com", "Password"), 300));

        var player1 = new Player(user1.getId(), user1.getNickname(), new ResourceSet(100, 100, 100));
        var player2 = new Player(user2.getId(), user2.getNickname(), new ResourceSet(100, 100, 100));

        var stateManager = new StateManager(
                GameState.FINISHED,
                new Date(Long.MAX_VALUE),
                player1.getId(),
                ""
        );
        stateManager.setWinnerId(player1.getId());
        var game = new Game(
                List.of(player1, player2),
                BoardUtils.generateEmptyPlain(3),
                stateManager
        );
        tested.shiftPlayersRankingsAfterGameFinished(game);

        assertEquals(310, applicationUserRepository.findById(user1.getId()).orElseThrow().getRating());
        assertEquals(290, applicationUserRepository.findById(user2.getId()).orElseThrow().getRating());
    }

    @Test
    void shiftPlayersRankingsAfterGameFinishedSecondWins() {
        var user1 = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password"), 300));
        var user2 = applicationUserRepository.save(new ApplicationUser("schabina", new LoginData("schabina@gmail.com", "Password"), 300));

        var player1 = new Player(user1.getId(), user1.getNickname(), new ResourceSet(100, 100, 100));
        var player2 = new Player(user2.getId(), user2.getNickname(), new ResourceSet(100, 100, 100));

        var stateManager = new StateManager(
                GameState.FINISHED,
                new Date(Long.MAX_VALUE),
                player1.getId(),
                ""
        );
        stateManager.setWinnerId(player2.getId());
        var game = new Game(
                List.of(player1, player2),
                BoardUtils.generateEmptyPlain(3),
                stateManager
        );
        tested.shiftPlayersRankingsAfterGameFinished(game);

        assertEquals(290, applicationUserRepository.findById(user1.getId()).orElseThrow().getRating());
        assertEquals(310, applicationUserRepository.findById(user2.getId()).orElseThrow().getRating());
    }

    @Test
    void getRankingAdjustments() {
        // stronger wins
        assertEquals(Pair.of(315, 290), tested.getRankingAdjustments(305, 300, true));
        assertEquals(Pair.of(292, 331), tested.getRankingAdjustments(300, 323, false));
        assertEquals(Pair.of(376, 294), tested.getRankingAdjustments(370, 300, true));

        // weaker wins
        assertEquals(Pair.of(404, 316), tested.getRankingAdjustments(420, 300, false));
        assertEquals(Pair.of(127, 283), tested.getRankingAdjustments(110, 300, true));
        assertEquals(Pair.of(1181, 319), tested.getRankingAdjustments(1200, 300, false));
    }

    @Test
    void getRankingNormalPath() {
        var user1 = applicationUserRepository.save(new ApplicationUser("macior", new LoginData("macior@gmail.com", "Password"), 340));
        var user2 = applicationUserRepository.save(new ApplicationUser("schabina", new LoginData("schabina@gmail.com", "Password"), 270));
        var user3 = applicationUserRepository.save(new ApplicationUser("melchior", new LoginData("melchiora@gmail.com", "Password"), 590));
        var rankings = tested.getRanking();
        assertEquals(List.of(
                new RankingEntry(user3.getId(), "melchior", 590),
                new RankingEntry(user1.getId(), "macior", 340),
                new RankingEntry(user2.getId(), "schabina", 270)
        ), rankings);
    }
}