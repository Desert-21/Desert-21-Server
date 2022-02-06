package com.github.maciejmalewicz.Desert21.repository;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {

}
