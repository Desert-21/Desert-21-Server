package com.github.maciejmalewicz.Desert21.domain.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginData {

    private String email;
    private String password;
}
