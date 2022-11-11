package com.github.maciejmalewicz.Desert21.repository;

import com.github.maciejmalewicz.Desert21.domain.users.GameInvitation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameInvitationRepository extends MongoRepository<GameInvitation, String> {
}
