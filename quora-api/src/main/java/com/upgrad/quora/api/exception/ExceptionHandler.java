package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(
            SignUpRestrictedException exc, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),HttpStatus.CONFLICT );

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(
            AuthenticationFailedException exc, WebRequest webRequest){

        if(exc.getCode() == "ATH-001"){
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),HttpStatus.BAD_REQUEST );
        }else{
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),HttpStatus.UNAUTHORIZED );
        }



    }



    @org.springframework.web.bind.annotation.ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signoutRestrictedException(
            SignOutRestrictedException exc, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),HttpStatus.UNAUTHORIZED );

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> usernotFoundException(
            UserNotFoundException exc, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),HttpStatus.NOT_FOUND );

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(
            AuthorizationFailedException exc, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),HttpStatus.FORBIDDEN );

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> invalidQuestionException(
            InvalidQuestionException exc, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),HttpStatus.NOT_FOUND );

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> answerNotFoundException(
            AnswerNotFoundException exc, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),HttpStatus.NOT_FOUND );

    }
}
