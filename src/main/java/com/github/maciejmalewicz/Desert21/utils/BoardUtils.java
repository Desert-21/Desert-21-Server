package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.game.BuildingDto;
import com.github.maciejmalewicz.Desert21.misc.Location;

import java.util.List;
import java.util.stream.Collectors;

public class BoardUtils {

    public static boolean ownsAtLeastOneLocation(Field[][] allFields, List<Location> locations, Player player) {
        return fieldsAtLocations(allFields, locations).stream()
                .filter(f -> f.getOwnerId().equals(player.getId()))
                .anyMatch(p -> true);
    }

    public static List<Field> fieldsAtLocations(Field[][] allFields, List<Location> locations) {
        return locations.stream()
                .filter(l -> isWithinBoardBounds(allFields, l))
                .map(l -> allFields[l.row()][l.col()])
                .collect(Collectors.toList());
    }

    public static boolean isWithinBoardBounds(Field[][] allFields, Location location) {
        return LocationUtils.isWithinBounds(location, 0, 0, allFields.length, allFields[0].length - 1);
    }
}
