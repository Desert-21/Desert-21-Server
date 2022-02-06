package com.github.maciejmalewicz.Desert21.repository;

import com.github.maciejmalewicz.Desert21.domain.accountManagement.AccountAcceptanceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountAcceptanceRequestRepository extends MongoRepository<AccountAcceptanceRequest, String> {
    Optional<AccountAcceptanceRequest> findFirstByNickname(String nickname);
    Optional<AccountAcceptanceRequest> findFirstByEmail(String email);
}
