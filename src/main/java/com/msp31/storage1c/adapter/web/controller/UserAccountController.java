package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.domain.dto.request.UserAuthenticationRequest;
import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import com.msp31.storage1c.domain.dto.response.UserInfoResponse;
import com.msp31.storage1c.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

import static com.msp31.storage1c.domain.dto.response.ResponseModel.ok;

@ApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAccountController {

    UserAccountService userAccountService;
    AuthenticationManager authenticationManager;

    @PostMapping("/user/register")
    public ResponseModel<UserInfoResponse> registerAccount(@Valid @RequestBody UserRegistrationRequest request) {
        return ok(userAccountService.registerUser(request));
    }

    @PostMapping("/user/login")
    public ResponseEntity<ResponseModel<Object>> login(
            @Valid @RequestBody UserAuthenticationRequest authRequest, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        token.setDetails(new WebAuthenticationDetails(request));

        try {
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(auth);
            if (auth.isAuthenticated()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
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

        return ResponseEntity.ok(ResponseModel.ok(userAccountService.getUserInfo(authRequest.getUsername())));
    }

    @GetMapping("/user/logout")
    public ResponseModel<Object> logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseModel.ok(null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/me")
    public ResponseModel<UserInfoResponse> getCurrentUserInfo(Principal principal) {
        return ResponseModel.ok(userAccountService.getUserInfo(principal.getName()));
    }
}
