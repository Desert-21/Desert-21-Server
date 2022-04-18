package com.github.maciejmalewicz.Desert21.service.gameSnapshot;

import com.github.maciejmalewicz.Desert21.config.gameBalance.GeneralConfiguration;
import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;
import com.github.maciejmalewicz.Desert21.utils.RandomGenerator;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import static com.github.maciejmalewicz.Desert21.utils.BoardUtils.ownsAtLeastOneLocation;

@Service
public class ArmySnapshotProcessingService {

    private final GeneralConfiguration generalConfiguration;

    public ArmySnapshotProcessingService(GeneralConfiguration generalConfiguration) {
        this.generalConfiguration = generalConfiguration;
    }

    public Army snapshotArmy(Player player, Field[][] allFields, Location location) {
        if (!BoardUtils.isWithinBoardBounds(allFields, location)) {
            return null;
        }

        var field = allFields[location.row()][location.col()];
        var army = field.getArmy();

        if (field.getOwnerId() == null || army == null) {
            return null;
        }

        if (field.getOwnerId().equals(player.getId())) {
            return army;
        }

        var distance = geometricDistanceFromPlayerCappedTo3(player, allFields, location);
        if (distance == 3) {
            return null;
        }
        if (army.isEmpty()) {
            return new Army(0, 0, 0);
        }
        var multiplier = randomMultiplier(distance);
        return new Army(
                multiplyByMultiplier(army.getDroids(), multiplier),
                multiplyByMultiplier(army.getTanks(), multiplier),
                multiplyByMultiplier(army.getCannons(), multiplier)
        );
    }

    private int multiplyByMultiplier(int amount, int multiplier) {
        var multiplied = (double)(amount * multiplier);
        var divided = multiplied / 100.0;
        return (int)Math.round(divided);
    }

    private int randomMultiplier(int distance) {
        var percentage = getBiasPercentage(distance);
        var range = getRandomMultiplierRange(percentage);
        return RandomGenerator.generateBetween(range.getFirst(), range.getSecond());
    }

    private Pair<Integer, Integer> getRandomMultiplierRange(int bias) {
        var isUpperRange = RandomGenerator.generateTrueOrFalse();
        int from;
        int to;
        if (isUpperRange) {
            from = 100;
            to = Math.round(10000f / (100f-bias));
        } else {
            from = Math.round(10000f / (100f+bias));
            to = 100;
        }
        return Pair.of(from, to);
    }

    private int getBiasPercentage(int distance) {
        if (distance == 1) {
            return generalConfiguration.generalConfig().getFogOfWar1();
        }
        if (distance == 2) {
            return generalConfiguration.generalConfig().getFogOfWar2();
        }
        return 0;
    }

    private int geometricDistanceFromPlayerCappedTo3(Player player, Field[][] allFields, Location location) {
        var locations1stLevel = LocationUtils.get1stLevelNeighbouringLocations(location);
        var ownsDistance1 = ownsAtLeastOneLocation(allFields, locations1stLevel, player);
        if (ownsDistance1) {
            return 1;
        }

        var locations2ndLevel = LocationUtils.get2ndLevelNeighbouringLocations(location);
        var ownsDistance2 = ownsAtLeastOneLocation(allFields, locations2ndLevel, player);
        if (ownsDistance2) {
            return 2;
        }

        return 3;
    }
}
