package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.dto.RankingEntry;
import com.github.maciejmalewicz.Desert21.models.RankingCategory;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RankingService {

    private final ApplicationUserRepository applicationUserRepository;

    private final List<RankingCategory> rankingCalculationLedger = List.of(
            new RankingCategory(Integer.MIN_VALUE, 10, 10, 10),
            new RankingCategory(11, 20, 11, 9),
            new RankingCategory(21, 35, 12, 8),
            new RankingCategory(36, 55, 13, 7),
            new RankingCategory(56, 80, 14, 6),
            new RankingCategory(81, 110, 15, 5),
            new RankingCategory(111, 145, 16, 4),
            new RankingCategory(146, 195, 17, 3),
            new RankingCategory(196, 240, 18, 2),
            new RankingCategory(240, Integer.MAX_VALUE, 19, 1)
    );

    public RankingService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    public void shiftPlayersRankingsAfterGameFinished(Game game) {
        var winner = game.getStateManager().getWinnerId();
        if (winner == null) {
            return;
        }
        var player1 = game.getPlayers().get(0);
        var player2 = game.getPlayers().get(1);
        var hasPlayer1Won = winner.equals(player1.getId());

        var user1 = applicationUserRepository.findById(player1.getId()).orElseThrow();
        var user2 = applicationUserRepository.findById(player2.getId()).orElseThrow();

        var newRankings = getRankingAdjustments(user1.getRating(), user2.getRating(), hasPlayer1Won);
        user1.setRating(newRankings.getFirst());
        user2.setRating(newRankings.getSecond());

        applicationUserRepository.save(user1);
        applicationUserRepository.save(user2);
    }

    public Pair<Integer, Integer> getRankingAdjustments(int player1Ranking, int player2Ranking, boolean hasPlayer1Won) {
        var hasPlayer1HigherRanking = player1Ranking > player2Ranking;
        var hasStrongerWon = (hasPlayer1HigherRanking && hasPlayer1Won)
                || (!hasPlayer1HigherRanking && !hasPlayer1Won);
        var rankingsDiff = Math.abs(player1Ranking - player2Ranking);
        var ledgerCategory = rankingCalculationLedger.stream()
                .filter(cat -> rankingsDiff >= cat.from() && rankingsDiff <= cat.to())
                .findAny()
                .orElseThrow();
        var rankingPointsToAddAndSubtract = hasStrongerWon ? ledgerCategory.strongerWonPoints() : ledgerCategory.weakerWonPoints();
        var newPlayer1Ranking = hasPlayer1Won ? player1Ranking + rankingPointsToAddAndSubtract : player1Ranking - rankingPointsToAddAndSubtract;
        var newPlayer2Ranking = hasPlayer1Won ? player2Ranking - rankingPointsToAddAndSubtract : player2Ranking + rankingPointsToAddAndSubtract;
        return Pair.of(
                Math.max(newPlayer1Ranking, 1),
                Math.max(newPlayer2Ranking, 1)
        );
    }

    public List<RankingEntry> getRanking() {
        return applicationUserRepository.findAll().stream()
                .map(entry -> new RankingEntry(entry.getId(), entry.getNickname(), entry.getRating()))
                .sorted(Comparator.comparingInt(RankingEntry::ranking).reversed())
                .toList();
    }
}
