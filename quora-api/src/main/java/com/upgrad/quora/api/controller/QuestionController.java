package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
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
import java.util.ListIterator;
import java.util.UUID;

@RestController
@RequestMapping(path = "/question")
public class QuestionController {

    @Autowired
    QuestionBusinessService questionBusinessService;

    @Autowired
    UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/create"
            , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") String authorization
            , QuestionRequest questionRequest) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());
        ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDateTime(now);
        QuestionEntity question = questionBusinessService.createQuestion(authorization, questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(question.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/all"
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
            @RequestHeader("authorization") String authorization)
            throws AuthorizationFailedException {

        userBusinessService.authorizeUser(authorization);

        List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestion();

        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();

        ListIterator<QuestionEntity> questionEntityListIterator = questionEntities.listIterator();

        while (questionEntityListIterator.hasNext()) {
            QuestionEntity questionEntity = questionEntityListIterator.next();
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .content(questionEntity.getContent())
                    .id(questionEntity.getUuid());
            questionDetailsResponses.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}"
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(
            @RequestHeader("authorization") String authorization,
            @PathVariable(value = "questionId") String questionId
            , QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthenticationEntity userAuthenticationEntity = userBusinessService.authorizeUser(authorization);
        QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(questionId);
        questionEntity.setContent(questionEditRequest.getContent());

        QuestionEntity updatedQuestionEntity = questionBusinessService.editQuestion(questionEntity, userAuthenticationEntity.getUserEntity());

        QuestionEditResponse editedQuestion = new QuestionEditResponse().id(updatedQuestionEntity.getUuid())
                .status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(editedQuestion, HttpStatus.OK);


    }

}
