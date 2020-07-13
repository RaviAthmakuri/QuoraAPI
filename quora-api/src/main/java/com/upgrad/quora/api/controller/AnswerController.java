package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @Autowired
    private UserBusinessService userAuthBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/question/{questionId}/answer/create",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionId,
            final AnswerRequest answerRequest)
            throws AuthorizationFailedException, InvalidQuestionException {


        final UserAuthenticationEntity userAuthEntity =
                userAuthBusinessService.authorizeUser(authorization,"CreateAnswer");

        QuestionEntity questionEntity = questionBusinessService.CheckValidQuestion(questionId);

        UserEntity userEntity = userAuthEntity.getUser();

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUser(userEntity);

        AnswerEntity createdAnswer = answerBusinessService.createAnswer(answerEntity);
        AnswerResponse answerResponse =
                new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.PUT,
            path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("answerId") final String answerId,
            final AnswerEditRequest answerEditRequest)
            throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthenticationEntity userAuthEntity = userAuthBusinessService.authorizeUser(authorization,"EditAnswer");



        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(answerId);
        answerEntity.setAnswer(answerEditRequest.getContent());
        answerEntity.setDate(ZonedDateTime.now());
        AnswerEntity updatedAnswer = answerBusinessService.updateAnswer(answerEntity, userAuthEntity);

        AnswerEditResponse answerEditResponse =
                new AnswerEditResponse().id(updatedAnswer.getUuid()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("answerId") final String answerId)
            throws AuthorizationFailedException, AnswerNotFoundException {
        // Authorize User, if user registered and Logged in?

        final UserAuthenticationEntity userAuthEntity =
                userAuthBusinessService.authorizeUser(authorization,"DeleteAnswer");

        AnswerEntity deletedAnswer = answerBusinessService.deleteAnswer(answerId, userAuthEntity.getUserEntity());
        AnswerDeleteResponse answerDeleteResponse =
                new AnswerDeleteResponse().id(deletedAnswer.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);


    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "answer/all/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthenticationEntity userAuthEntity = userAuthBusinessService.authorizeUser(authorization,"GetAllAnswersToQuestion");
        QuestionEntity questionEntity = questionBusinessService.CheckValidQuestion(questionId);

        List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<>();
        List<AnswerEntity> answers = answerBusinessService.getAllAnswersToQuestion(questionId, userAuthEntity);

        for (AnswerEntity answer : answers) {
            AnswerDetailsResponse detailsResponse = new AnswerDetailsResponse();
            detailsResponse.setId(answer.getUuid());
            detailsResponse.setAnswerContent(answer.getAnswer());
            detailsResponse.setQuestionContent(questionEntity.getContent());
            answerDetailsResponseList.add(detailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList, HttpStatus.OK);
    }
}