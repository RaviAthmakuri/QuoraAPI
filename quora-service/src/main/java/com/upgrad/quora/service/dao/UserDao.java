package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserAuthenticationEntity persistUserAuth(
            UserAuthenticationEntity userAuthenticationEntity) {
        entityManager.persist(userAuthenticationEntity);
        return userAuthenticationEntity;
    }

    public UserAuthenticationEntity getUserAuth(final String accessToken) {
        try {
            return entityManager
                    .createNamedQuery("userAuthByAccessToken", UserAuthenticationEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthenticationEntity setUserLogout(final UserAuthenticationEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public UserEntity getUserByEmail(String email) {
        try {
            UserEntity existingUser =
                    entityManager
                            .createNamedQuery("userByEmail", UserEntity.class)
                            .setParameter("email", email)
                            .getSingleResult();

            return existingUser;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByUserName(String userName) {
        try {
            UserEntity existingUser =
                    entityManager
                            .createNamedQuery("userByUserName", UserEntity.class)
                            .setParameter("userName", userName)
                            .getSingleResult();

            return existingUser;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByUuid(final String uuid) {
        try {
            return entityManager
                    .createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
