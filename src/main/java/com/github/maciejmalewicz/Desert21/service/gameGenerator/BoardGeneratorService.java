package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.misc.Location;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.RandomGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.maciejmalewicz.Desert21.misc.BuildingType.EMPTY_FIELD;

@Service
public class BoardGeneratorService {

    private final BasicBoardGeneratorConfig config;


    public BoardGeneratorService(BasicBoardGeneratorConfig config) {
        this.config = config;
    }

    public Field[][] generateBoard(Player player1, Player player2) {
        var fieldsWithBuildings = feedFieldsWithBuildings(
                BoardUtils.generateEmptyPlain(config.getSize()),
                config.getBoardLocationRules()
        );
        assignFieldsToPlayer(player1, fieldsWithBuildings, config.getPLayer1Locations());
        assignFieldsToPlayer(player2, fieldsWithBuildings, config.getPLayer2Locations());
        return fieldsWithBuildings;
    }

    private void assignFieldsToPlayer(Player player, Field[][] fields, List<Location> locations) {
        locations.forEach(l -> {
            fields[l.row()][l.col()].setOwnerId(player.getId());
        });
    }

    //no fold in Java :(
    private Field[][] feedFieldsWithBuildings(Field[][] fields, List<BoardLocationRule> rules) {
        for (BoardLocationRule rule : rules) {
            applyRuleToFields(fields, rule);
        }
        return fields;
    }

    private void applyRuleToFields(Field[][] fields, BoardLocationRule rule) {
        var availableFields = rule.availableLocations().stream()
                .map(location -> fields[location.row()][location.col()])
                .filter(field -> field.getBuilding().getType().equals(EMPTY_FIELD))
                .collect(Collectors.toList());
        IntStream.range(0, rule.amount()).forEach(i -> {
            int size = availableFields.size();
            int index = RandomGenerator.generateBetween(0, size - 1);
            var fieldToMark = availableFields.get(index);
            fieldToMark.setBuilding(new Building(rule.buildingType()));
            availableFields.remove(fieldToMark);
        });
    }
}
