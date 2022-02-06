package com.github.maciejmalewicz.Desert21.repository;

import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends MongoRepository<ApplicationUser, String> {
    Optional<ApplicationUser> findFirstByNickname(String nickname);

    @Query("{'loginData.email' : ?0}")
    Optional<ApplicationUser> findFirstByEmail(String email);
}
