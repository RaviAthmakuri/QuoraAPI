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
import com.upgrad.quora.service.exception.UserNotFoundException;
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
  @Autowired private AnswerBusinessService answerBusinessService;

  @Autowired private UserBusinessService userAuthBusinessService;

  @Autowired private QuestionBusinessService questionBusinessService;

  @RequestMapping(
      method = RequestMethod.POST,
      path = "/question/{questionId}/answer/create",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer(
      @RequestHeader("authorization") final String authorization,
      @PathVariable("questionId") final String questionId,
      final AnswerRequest answerRequest)
      throws AuthorizationFailedException, InvalidQuestionException {


    final UserAuthenticationEntity userAuthEntity = userAuthBusinessService.getUser(authorization);

    final UserAuthenticationEntity userAuthEntity =
        userAuthBusinessService.authorizeUser(authorization);

    QuestionEntity questionEntity = questionBusinessService.CheckValidQuestion(questionId);

    UserEntity userEntity = userAuthEntity.getUser();

    AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setUuid(UUID.randomUUID().toString());
    answerEntity.setDate(ZonedDateTime.now());
    answerEntity.setQuestion(questionEntity);
    answerEntity.setAnswer(answerRequest.getAnswer());
    answerEntity.setUser(userEntity);

    AnswerEntity createdAnswer = answerBusinessService.createAnswer(answerEntity, userAuthEntity);
    AnswerResponse answerResponse =
        new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");

    return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
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
        userAuthBusinessService.authorizeUser(authorization);

    // Get the Logged In User Details
    final UserEntity currentUser = userAuthEntity.getUser();

    // Check if the USer is Admin Role
    final boolean isAdminUser = "Admin".equalsIgnoreCase(currentUser.getRole());

    // Load the current Answer to be Deleted.
    AnswerEntity answerEntity = answerBusinessService.getAnswerEntity(answerId);

    // Lets Check if the user is the Owner of this Answer
    final boolean isAnswerOwner = currentUser.getUuid().equals(answerEntity.getUser().getUuid());

    // Only a Admin User or the Answer Owner Can delete the Answer.
    if (isAdminUser || isAnswerOwner) {

      // Lets Delete the Answer
      AnswerDeleteResponse answerDeleteResponse = null;
      AnswerEntity deletedAnswer = this.answerBusinessService.deleteAnswer(answerEntity);
      if (deletedAnswer == null) {
        answerDeleteResponse =
            new AnswerDeleteResponse()
                .id(answerEntity.getUuid())
                .status("ANSWER COULD NOT BE DELETED");
      } else {
        answerDeleteResponse =
            new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
      }

      return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }
    throw new AuthorizationFailedException(
        "ATHR-003", "Only the answer owner or admin can delete the answer");
=======
      method = RequestMethod.PUT,
      path = "/answer/edit/{answerId}",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerEditResponse> editAnswer(
      @RequestHeader("authorization") final String authorization,
      @PathVariable("answerId") final String answerId,
      final AnswerEditRequest answerEditRequest)
      throws AuthorizationFailedException, AnswerNotFoundException {

    UserAuthenticationEntity userAuthEntity = userAuthBusinessService.authorizeUser(authorization);

    AnswerEntity answerEntity = answerBusinessService.getAnswerById(answerId);
    AnswerEntity checkedAnswer =
        answerBusinessService.verifyAnswerBelongsToUser(userAuthEntity, answerEntity);

    checkedAnswer.setAnswer(answerEditRequest.getContent());
    AnswerEntity updatedAnswer = answerBusinessService.updateAnswer(checkedAnswer);

    AnswerEditResponse answerEditResponse =
        new AnswerEditResponse().id(updatedAnswer.getUuid()).status("ANSWER EDITED");

    return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
  }

  @RequestMapping(
      method = RequestMethod.GET,
      path = "answer/all/{questionId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
      @RequestHeader("authorization") final String authorization,
      @PathVariable("questionId") final String questionId)
      throws AuthorizationFailedException, InvalidQuestionException {

    UserAuthenticationEntity userAuthEntity = userAuthBusinessService.authorizeUser(authorization);
    QuestionEntity questionEntity = questionBusinessService.CheckValidQuestion(questionId);

    ArrayList<AnswerDetailsResponse> list = new ArrayList<>();
    ArrayList<AnswerEntity> answers =
        (ArrayList) answerBusinessService.getAllAnswersToQuestion(questionId, userAuthEntity);

    for (AnswerEntity answer : answers) {
      AnswerDetailsResponse detailsResponse = new AnswerDetailsResponse();
      detailsResponse.setId(answer.getUuid());
      detailsResponse.setAnswerContent(answer.getAnswer());
      detailsResponse.setQuestionContent(questionEntity.getContent());
      list.add(detailsResponse);
    }

    return new ResponseEntity<List<AnswerDetailsResponse>>(list, HttpStatus.OK);
  }
}
