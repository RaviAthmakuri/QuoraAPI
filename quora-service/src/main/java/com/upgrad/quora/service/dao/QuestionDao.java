package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity inQuestionEntity) {
        entityManager.persist(inQuestionEntity);
        return inQuestionEntity;
    }

    public QuestionEntity getQuestionByUuid(String questionId) {
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class)
                    .setParameter("questionId", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity editQuestion(QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }

    public List<QuestionEntity> getAllQuestion() {
        try {
            List<QuestionEntity> questionEntities = entityManager
                    .createQuery("SELECT Q from QuestionEntity Q", QuestionEntity.class)
                    .getResultList();
            return questionEntities;

        } catch (NoResultException nre) {
            return null;
        }

    }

    public QuestionEntity deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestionByUser(String uuid) {
        try {
            List<QuestionEntity> questionEntities = entityManager
                    .createQuery("SELECT Q from QuestionEntity Q WHERE Q.userEntity.uuid = :uuid", QuestionEntity.class)
                    .setParameter("uuid", uuid)
                    .getResultList();

            return questionEntities;

        } catch (NoResultException nre) {
            return null;
        }
    }
}
