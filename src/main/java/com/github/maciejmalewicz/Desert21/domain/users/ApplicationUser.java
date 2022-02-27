package com.github.maciejmalewicz.Desert21.domain.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

import static com.github.maciejmalewicz.Desert21.config.Constants.USER_ID_AUTH_PREFIX;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("app_users")
public class ApplicationUser implements UserDetails {
    @Id
    private String id;
    private String nickname;
    private LoginData loginData;

    public ApplicationUser(String nickname, LoginData loginData) {
        this.nickname = nickname;
        this.loginData = loginData;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(
          new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + id)
        );
    }

    @Override
    public String getPassword() {
        return loginData.getPassword();
    }

    @Override
    public String getUsername() {
        return loginData.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
