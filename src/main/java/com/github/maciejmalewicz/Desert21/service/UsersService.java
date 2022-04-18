package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.UsersData;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final ApplicationUserRepository userRepository;

    public UsersService(ApplicationUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UsersData getUsersData(String email) throws NotAcceptableException {
        return userRepository.findFirstByEmail(email)
                .map(this::mapToUsersData)
                .orElseThrow(() -> new NotAcceptableException("User not found!"));
    }

    private UsersData mapToUsersData(ApplicationUser user) {
        return new UsersData(
                user.getId(),
                user.getNickname()
        );
    }
}
