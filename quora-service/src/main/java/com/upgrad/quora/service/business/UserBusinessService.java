package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@Transactional
public class UserBusinessService {

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;


    @Autowired
    private UserDao userDao;

    public UserAuthenticationEntity authorizeUser(final String accessToken, String detailType)
            throws AuthorizationFailedException {

        UserAuthenticationEntity userByToken = userDao.getUserByToken(accessToken);


        if (userByToken == null) {

            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        } else if (userByToken.getLogoutAt() != null) {

            if (detailType.equalsIgnoreCase("UserDetails")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            } else if (detailType.equalsIgnoreCase("CreateQuestion")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            } else if (detailType.equalsIgnoreCase("GetAllQuestions")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
            } else if (detailType.equalsIgnoreCase("EditQuestion")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            } else if (detailType.equalsIgnoreCase("DeleteQuestion")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
            } else if (detailType.equalsIgnoreCase("GetAllQuestionsByUser")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
            } else if (detailType.equalsIgnoreCase("CreateAnswer")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            } else if (detailType.equalsIgnoreCase("EditAnswer")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
            } else if (detailType.equalsIgnoreCase("DeleteAnswer")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
            } else if (detailType.equalsIgnoreCase("GetAllAnswersToQuestion")) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
            }


        }

        return userByToken;

    }

    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException {

        if (userDao.getUserByUserName(userEntity.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001"
                    , "Try any other Username, this Username has already been taken");
        } else if (userDao.getUserByEmail(userEntity.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002",
                    "This user has already been registered, try with any other emailId");
        }

        String password = userEntity.getPassword();

        String[] encrypt = passwordCryptographyProvider.encrypt(password);
        userEntity.setSalt(encrypt[0]);
        userEntity.setPassword(encrypt[1]);

        return userDao.createUser(userEntity);

    }

    public UserAuthenticationEntity authenticateUser(String userName, String password) throws AuthenticationFailedException {
        UserEntity userByUserName = userDao.getUserByUserName(userName);

        if (userByUserName == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        } else {

            String encryptedPassword = passwordCryptographyProvider.encrypt(password, userByUserName.getSalt());
            if (encryptedPassword.equals(userByUserName.getPassword())) {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
                ZonedDateTime now = ZonedDateTime.now();
                final ZonedDateTime expiresAt = now.plusHours(8);
                UserAuthenticationEntity userAuthenticationEntity = new UserAuthenticationEntity();
                userAuthenticationEntity.setUserEntity(userByUserName);
                userAuthenticationEntity.setUuid(userByUserName.getUuid());
                userAuthenticationEntity.setLoginAt(now);
                userAuthenticationEntity.setExpiresAt(expiresAt);
                userAuthenticationEntity.setAccessToken(jwtTokenProvider.
                        generateToken(userByUserName.getUuid(), now, expiresAt));

                userDao.persistUserAuth(userAuthenticationEntity);

                return userAuthenticationEntity;

            } else {
                throw new AuthenticationFailedException("ATH-002", "Password failed");
            }


        }

    }

    public UserAuthenticationEntity updateLogoutUser(String accessToken) throws SignOutRestrictedException {

        UserAuthenticationEntity userByToken = userDao.getUserByToken(accessToken);
        if (userByToken == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        ZonedDateTime logOut;
        logOut = ZonedDateTime.now();
        userByToken.setLogoutAt(logOut);
        userDao.updateUserAuth(userByToken);
        return userByToken;
    }

    public UserEntity userProfile(String uuid, String accessToken)
            throws AuthorizationFailedException, UserNotFoundException {

        UserAuthenticationEntity userByToken = authorizeUser(accessToken, "UserDetails");

        UserEntity userEntity;
        userEntity = userDao.getUserByUuid(uuid);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }


        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(String uuid, String authorization)
            throws UserNotFoundException, AuthorizationFailedException {
        UserAuthenticationEntity userAuthEntity = userDao.getUserByToken(authorization);
        if (userAuthEntity != null) {
            if ((userAuthEntity.getLogoutAt() == null)) {
                UserEntity userEntity = userDao.getUserByUuid(uuid);
                if (userEntity == null) {
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                }
                String role = userAuthEntity.getUser().getRole();
                if (role.equalsIgnoreCase("admin")) {
                    userDao.deleteUser(userEntity);
                } else {
                    throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
                }
                return userEntity;
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

    }

    @Transactional
    public UserAuthenticationEntity getUser(final String authorizationToken) throws AuthorizationFailedException {
        UserAuthenticationEntity userAuthEntity = userDao.getUserByToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return userAuthEntity;
    }
}
