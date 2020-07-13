package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
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
  }
}
