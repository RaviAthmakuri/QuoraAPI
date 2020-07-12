package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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

    public List<QuestionEntity> getAllQuestion(){
        List<QuestionEntity> questionEntities = entityManager
                .createQuery("SELECT Q from QuestionEntity Q", QuestionEntity.class)
                .getResultList();

        return questionEntities;
    }
}
