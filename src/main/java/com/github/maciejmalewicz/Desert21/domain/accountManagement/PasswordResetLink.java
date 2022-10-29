package com.github.maciejmalewicz.Desert21.domain.accountManagement;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@NoArgsConstructor
@Data
@Document("password_reset_link")
@TypeAlias("password_reset_link")
public class PasswordResetLink {

    @Id
    private String id;
    private String email;
    private String activationCode;
    private Date expiryDate;
    private String userId;

    public PasswordResetLink(String email, String activationCode, Date expiryDate, String userId) {
        this.email = email;
        this.activationCode = activationCode;
        this.expiryDate = expiryDate;
        this.userId = userId;
    }
}
