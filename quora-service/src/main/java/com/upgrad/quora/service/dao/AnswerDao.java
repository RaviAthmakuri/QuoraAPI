package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import javax.persistence.Query;

import java.util.List;

@Repository
public class AnswerDao {
  @PersistenceContext private EntityManager entityManager;

  public AnswerEntity createAnswer(AnswerEntity answerEntity) {
    entityManager.persist(answerEntity);
    return answerEntity;
  }

  public AnswerEntity loadAnswer(String answerId) throws AnswerNotFoundException {
    AnswerEntity answerEntity = null;

    try {
      answerEntity =
          entityManager
              .createNamedQuery("getAnswerFromId", AnswerEntity.class)
              .setParameter("uuid", answerId)
              .getSingleResult();
    } catch (NoResultException e) {
      e.printStackTrace();
    }

    if (answerEntity == null)
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");

    return answerEntity;
  }


  public AnswerEntity deleteAnswer(AnswerEntity answerEntityTobeDeleted)  {
    try {
      this.entityManager.remove(answerEntityTobeDeleted);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return answerEntityTobeDeleted;
  }


  public AnswerEntity getAnswerById(String uuid) {
    try {
      return entityManager
          .createNamedQuery("getAnswerById", AnswerEntity.class)
          .setParameter("uuid", uuid)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  public AnswerEntity verifyAnswerBelongsToUser(String auuid, String uuuid) {

    try {
      return entityManager
          .createNamedQuery("verifyAnswerBelongsToUser", AnswerEntity.class)
          .setParameter("auuid", auuid)
          .setParameter("uuuid", uuuid)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  public AnswerEntity updateAnswer(AnswerEntity answerEntity) {
    return entityManager.merge(answerEntity);
  }

  public List<AnswerEntity> getAllAnswersToQuestion(String questionId) {
    return entityManager
        .createNamedQuery("getAllAnswersToQuestion", AnswerEntity.class)
        .setParameter("uuid", questionId)
        .getResultList();
  }

}
