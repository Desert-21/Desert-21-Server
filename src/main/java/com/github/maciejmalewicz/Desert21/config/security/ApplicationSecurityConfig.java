package com.github.maciejmalewicz.Desert21.config.security;

import com.github.maciejmalewicz.Desert21.config.security.jwt.JwtConfig;
import com.github.maciejmalewicz.Desert21.config.security.jwt.JwtTokenVerifier;
import com.github.maciejmalewicz.Desert21.config.security.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.github.maciejmalewicz.Desert21.service.ApplicationUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.crypto.SecretKey;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final JwtTokenVerifier tokenVerifier;
    private final CorsConfig corsConfig;

    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder,
                                     ApplicationUserDetailsService applicationUserDetailsService,
                                     SecretKey secretKey,
                                     JwtConfig jwtConfig, JwtTokenVerifier tokenVerifier, CorsConfig corsConfig) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = applicationUserDetailsService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.tokenVerifier = tokenVerifier;
        this.corsConfig = corsConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
                .addFilterAfter(tokenVerifier, JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/registration/users").permitAll()
                .antMatchers(HttpMethod.POST, "**/**", "/api/login").permitAll()
                .anyRequest().permitAll(); //todo CHANGE!!
    }

    //working for now, will be removed anyway when setting up CORS
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .exposedHeaders("Authorization")
                        .allowedOrigins(corsConfig.getOrigins().toArray(new String[0]));
            }
        };
    }

    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
