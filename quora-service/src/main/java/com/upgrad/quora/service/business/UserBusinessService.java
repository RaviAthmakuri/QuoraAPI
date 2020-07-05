package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@Transactional
public class UserBusinessService {

    @Autowired private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired private UserDao userDao;

    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException {

        if (userDao.getUserByUserName(userEntity.getUserName()) != null) {
            throw new SignUpRestrictedException(
                    "SGR-001", "Try any other Username, this Username has already been taken");
        } else if (userDao.getUserByEmail(userEntity.getEmail()) != null) {
            throw new SignUpRestrictedException(
                    "SGR-002", "This user has already been registered, try with any other emailId");
        }

        String password = userEntity.getPassword();

        String[] encrypt = passwordCryptographyProvider.encrypt(password);
        userEntity.setSalt(encrypt[0]);
        userEntity.setPassword(encrypt[1]);

        return userDao.createUser(userEntity);
    }

    public UserAuthenticationEntity authenticateUser(String userName, String password)
            throws AuthenticationFailedException {
        UserEntity userByUserName = userDao.getUserByUserName(userName);

        if (userByUserName == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        } else {

            String encryptedPassword =
                    PasswordCryptographyProvider.encrypt(password, userByUserName.getSalt());
            if (encryptedPassword.equals(userByUserName.getPassword())) {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
                ZonedDateTime now = ZonedDateTime.now();
                final ZonedDateTime expiresAt = now.plusHours(8);
                UserAuthenticationEntity userAuthenticationEntity = new UserAuthenticationEntity();
                userAuthenticationEntity.setUserEntity(userByUserName);
                userAuthenticationEntity.setUuid(userByUserName.getUuid());
                userAuthenticationEntity.setLoginAt(now);
                userAuthenticationEntity.setExpiresAt(expiresAt);
                userAuthenticationEntity.setAccessToken(
                        jwtTokenProvider.generateToken(userByUserName.getUuid(), now, expiresAt));

                userDao.persistUserAuth(userAuthenticationEntity);

                return userAuthenticationEntity;

            } else {
                throw new AuthenticationFailedException("ATH-002", "Password failed");
            }
        }
    }

    public UserEntity userProfile(String uuid, UserAuthenticationEntity userAuthEntity)
            throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = userDao.getUserByUuid(uuid);
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to get user details");
        } else if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return userEntity;
    }
}
