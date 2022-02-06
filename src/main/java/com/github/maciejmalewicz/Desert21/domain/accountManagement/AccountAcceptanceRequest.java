package com.github.maciejmalewicz.Desert21.domain.accountManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Data
@Document("account_acceptance_request")
@TypeAlias("account_acceptance_request")
public class AccountAcceptanceRequest {
    @Id
    private String id;
    private String email;
    private String nickname;
    private String password;
    private String activationCode;

    public AccountAcceptanceRequest(String email, String nickname, String password, String activationCode) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.activationCode = activationCode;
    }
}
