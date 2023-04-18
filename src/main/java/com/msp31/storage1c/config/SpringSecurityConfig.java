package com.msp31.storage1c.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {

    @Value("${storage1c.security.remember-me-key}")
    private String rememberMeKey;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity, TokenBasedRememberMeServices rememberMeServices) throws Exception {
        return httpSecurity.cors().and().csrf().disable()  // TODO configure cors and csrf properly
                .anonymous().disable()
                .authorizeHttpRequests()
                .anyRequest().permitAll().and()
                .logout().disable()
                .rememberMe().rememberMeServices(rememberMeServices).and()
                .build();
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        var rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256);
        rememberMe.setTokenValiditySeconds(2592000);
        rememberMe.setAlwaysRemember(true);
        return rememberMe;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
