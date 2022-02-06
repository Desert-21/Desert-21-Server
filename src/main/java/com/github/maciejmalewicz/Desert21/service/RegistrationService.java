package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.accountManagement.AccountAcceptanceRequest;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.dto.AccountActivationDto;
import com.github.maciejmalewicz.Desert21.dto.RegistrationDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.AccountAcceptanceRequestRepository;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.service.email.AccountActivationEmailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.UUID;

@Service
public class RegistrationService {

    private final ApplicationUserRepository applicationUserRepository;
    private final AccountAcceptanceRequestRepository accountAcceptanceRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountActivationEmailSender accountActivationEmailSender;

    public RegistrationService(
            ApplicationUserRepository applicationUserRepository,
            AccountAcceptanceRequestRepository accountAcceptanceRequestRepository,
            PasswordEncoder passwordEncoder,
            AccountActivationEmailSender accountActivationEmailSender) {
        this.applicationUserRepository = applicationUserRepository;
        this.accountAcceptanceRequestRepository = accountAcceptanceRequestRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountActivationEmailSender = accountActivationEmailSender;
    }

    @Transactional
    public void registerUser(RegistrationDto dto) throws NotAcceptableException {
        validateEmailAndNickname(dto);
        var activationCode = UUID.randomUUID().toString();
        var passwordEncoded = passwordEncoder.encode(dto.password());
        var acceptanceRequest = new AccountAcceptanceRequest(
          dto.email(),
          dto.nickname(),
          passwordEncoded,
          activationCode
        );
        try {
            accountActivationEmailSender.sendActivationCode(dto.email(), dto.nickname(), activationCode);
        } catch (MessagingException exc) {
            throw new NotAcceptableException(String.format("Could not send an email to address: %s", dto.email()));
        }
        accountAcceptanceRequestRepository.save(acceptanceRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    public void activateAccount(AccountActivationDto dto) throws NotAcceptableException {
        var request = accountAcceptanceRequestRepository.findFirstByEmail(dto.email())
                .orElseThrow(() -> new NotAcceptableException("Account creation request with given email not found!"));
        if (!request.getActivationCode().equals(dto.activationCode())) {
            throw new NotAcceptableException("Activation codes didn't match!");
        }
        transformActivationRequestIntoAccount(request);
    }

    private void transformActivationRequestIntoAccount(AccountAcceptanceRequest request) {
        accountAcceptanceRequestRepository.deleteById(request.getId());
        var user = new ApplicationUser(
                request.getNickname(),
                new LoginData(
                        request.getEmail(),
                        request.getPassword()
                ));
        applicationUserRepository.save(user);
    }

    private void validateEmailAndNickname(RegistrationDto dto) throws NotAcceptableException {
        var acceptanceRequestByEmail = accountAcceptanceRequestRepository.findFirstByEmail(dto.email());
        var applicationUserByEmail = applicationUserRepository.findFirstByEmail(dto.email());
        if (acceptanceRequestByEmail.isPresent() || applicationUserByEmail.isPresent()) {
            throw new NotAcceptableException("Email already occupied!");
        }
        var acceptanceRequestByNickname = accountAcceptanceRequestRepository.findFirstByNickname(dto.nickname());
        var applicationUserByNickname = applicationUserRepository.findFirstByNickname(dto.nickname());
        if (acceptanceRequestByNickname.isPresent() || applicationUserByNickname.isPresent()) {
            throw new NotAcceptableException("Nickname already occupied!");
        }
    }
}
