package com.github.maciejmalewicz.Desert21.repository;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
//    @Query(value = "{ 'players' : { $elemMatch : { '_id' : ?0 } } }") // add active check
    @Query(value = "{ 'players' : { $elemMatch : { '_id' : ?0 } }, 'stateManager.gameState': { $ne : 'FINISHED' } }")
    Optional<Game> findByPlayersId(String playersId);
}
