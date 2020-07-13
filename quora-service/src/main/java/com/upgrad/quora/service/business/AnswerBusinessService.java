package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(
            AnswerEntity answerEntity)
            throws AuthorizationFailedException {
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswerById(String uuid) throws AnswerNotFoundException {
        AnswerEntity answerEntity = answerDao.getAnswerById(uuid);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        return answerEntity;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(AnswerEntity InAnswerEntity, UserAuthenticationEntity userAuthenticationEntity)
            throws AnswerNotFoundException, AuthorizationFailedException {

        AnswerEntity answerEntity = getAnswerById(InAnswerEntity.getUuid());

        answerEntity.setDate(InAnswerEntity.getDate());
        answerEntity.setAnswer(InAnswerEntity.getAnswer());
        if (answerEntity.getUser().getUuid() == userAuthenticationEntity.getUser().getUuid()) {
            AnswerEntity updateAnswer = answerDao.updateAnswer(answerEntity);
            return updateAnswer;
        } else {
            throw new AuthorizationFailedException(
                    "ATHR-003", "Only the answer owner can edit or delete the answer");
        }
    }

    public List<AnswerEntity> getAllAnswersToQuestion(
            String questionId, UserAuthenticationEntity userAuthEntity)
            throws AuthorizationFailedException {
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        return answerDao.getAllAnswersToQuestion(questionId);
    }


    @Transactional
    public AnswerEntity deleteAnswer(String answerId, UserEntity userEntity)
            throws AnswerNotFoundException, AuthorizationFailedException {

        AnswerEntity answerEntity = getAnswerById(answerId);
        // Lets Check if the user is the Owner of this Answer
        final boolean isAnswerOwner = answerEntity.getUser().getUuid().equals(userEntity.getUuid());
        // Check if the USer is Admin Role
        final boolean isAdminUser = userEntity.getRole().equalsIgnoreCase("admin");
        if ((isAnswerOwner)  || (isAdminUser)) {
            return answerDao.deleteAnswer(answerEntity);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

    }
}
