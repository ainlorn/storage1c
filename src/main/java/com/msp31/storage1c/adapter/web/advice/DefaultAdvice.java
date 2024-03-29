package com.msp31.storage1c.adapter.web.advice;

import com.msp31.storage1c.adapter.web.annotation.ApiAdvice;
import com.msp31.storage1c.common.constant.Status;
import com.msp31.storage1c.common.exception.BaseException;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.MethodNotAllowedException;


@ApiAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultAdvice {

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ResponseModel<Object>> baseException(BaseException e) {
        return AdviceUtils.createResponse(e.getStatus());
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<ResponseModel<Object>> accessDenied(Exception e) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))
            return AdviceUtils.createResponse(Status.ACCESS_DENIED);
        return AdviceUtils.createResponse(Status.UNAUTHORIZED);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ResponseModel<Object>> noBody(Exception e) {
        return AdviceUtils.createResponse(Status.MISSING_BODY);
    }

    @ExceptionHandler({MethodNotAllowedException.class})
    public ResponseEntity<ResponseModel<Object>> notAllowed(Exception e) {
        return AdviceUtils.createResponse(Status.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseModel<Object>> anyException(Exception e) {
        return AdviceUtils.createResponse(Status.UNKNOWN);
    }
}
