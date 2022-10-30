package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.accountManagement.PasswordResetLink;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.PasswordResetLinkRepository;
import com.github.maciejmalewicz.Desert21.service.email.PasswordResetEmailSender;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.Date;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final ApplicationUserRepository userRepository;
    private final PasswordResetLinkRepository passwordResetLinkRepository;
    private final PasswordResetEmailSender passwordResetEmailSender;
    private final PasswordEncoder passwordEncoder;


    public PasswordResetService(ApplicationUserRepository userRepository, PasswordResetLinkRepository passwordResetLinkRepository, PasswordResetEmailSender passwordResetEmailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetLinkRepository = passwordResetLinkRepository;
        this.passwordResetEmailSender = passwordResetEmailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void requestPasswordReset(String email) throws NotAcceptableException {
        var user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new NotAcceptableException("Could not find the email!"));
        var userId = user.getId();
        var nickname = user.getNickname();
        var code = UUID.randomUUID().toString();
        var expiryDate = DateUtils.millisecondsFromNow(300_000); // 5 minutes

        var passwordResetRequest = new PasswordResetLink(
                email,
                code,
                expiryDate,
                userId
        );

        var savedLink = passwordResetLinkRepository.save(passwordResetRequest);

        passwordResetEmailSender.sendPasswordResetEmail(code, savedLink.getId(), nickname, email);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void executePasswordReset(String email, String linkId, String linkCode, String password) throws NotAcceptableException {
        var link = passwordResetLinkRepository.findById(linkId)
                .orElseThrow(() -> new NotAcceptableException("Password reset link not found!"));
        if (!link.getActivationCode().equals(linkCode) || !link.getEmail().equals(email)) {
            throw new NotAcceptableException("Password reset link is invalid!");
        }
        var now = new Date();
        var expiryDate = link.getExpiryDate();
        if (now.after(expiryDate)) {
            throw new NotAcceptableException("Password reset link has expired!");
        }

        // now executing
        var userId = link.getUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotAcceptableException("User not found! Have you deleted your account?"));
        var loginData = user.getLoginData();
        var encoded = passwordEncoder.encode(password);
        loginData.setPassword(encoded);
        userRepository.save(user);
    }
}
