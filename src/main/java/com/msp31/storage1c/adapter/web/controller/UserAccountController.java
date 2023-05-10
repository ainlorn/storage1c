package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.domain.dto.request.UserAuthenticationRequest;
import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import com.msp31.storage1c.domain.dto.response.UserInfo;
import com.msp31.storage1c.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import static com.msp31.storage1c.domain.dto.response.ResponseModel.ok;

@ApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAccountController {

    UserService userService;
    AuthenticationManager authenticationManager;
    RememberMeServices rememberMeServices;

    /**
     * Зарегистрировать аккаунт пользователя
     */
    @PostMapping("/register")
    public ResponseModel<UserInfo> registerAccount(@Valid @RequestBody UserRegistrationRequest request) {
        return ok(userService.registerUser(request));
    }

    /**
     * Войти в аккаунт пользователя
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseModel<Object>> login(
            @Valid @RequestBody UserAuthenticationRequest authRequest, HttpServletRequest request,
            HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        token.setDetails(new WebAuthenticationDetails(request));

        try {
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(auth);
            if (auth != null && auth.isAuthenticated()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
                rememberMeServices.loginSuccess(request, response, auth);
            } else {
                throw new BadCredentialsException(null);
            }
        } catch (BadCredentialsException e) {
            SecurityContextHolder.getContext().setAuthentication(null);

            var status = Status.WRONG_CREDENTIALS;
            return ResponseEntity
                    .status(status.getHttpCode())
                    .body(ResponseModel.withStatus(status, null));
        }

        return ResponseEntity.ok(ResponseModel.ok(userService.getCurrentUserInfo()));
    }

    /**
     * Выйти из аккаунта
     */
    @GetMapping("/logout")
    public ResponseModel<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.getContext().setAuthentication(null);
        rememberMeServices.loginFail(request, response);
        return ResponseModel.ok(null);
    }
}
