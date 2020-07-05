package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthentication;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }


    public UserAuthentication persistUserAuth(UserAuthentication  userAuthentication){
        entityManager.persist(userAuthentication);
        return userAuthentication;

    }

    public UserEntity getUserByEmail(String email){
        try {
            UserEntity existingUser = entityManager.createNamedQuery("userByEmail", UserEntity.class)
                    .setParameter("email", email).getSingleResult();

            return existingUser;
        }catch(NoResultException nre){
            return  null;
        }
    }

    public UserEntity getUserByUserName(String userName){
        try {
            UserEntity existingUser = entityManager.createNamedQuery("userByUserName", UserEntity.class)
                    .setParameter("userName", userName).getSingleResult();

            return existingUser;
        }catch(NoResultException nre){
            return  null;
        }
    }
}
