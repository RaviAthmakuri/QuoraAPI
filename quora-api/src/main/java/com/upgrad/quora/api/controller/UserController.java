package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private  UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.POST
            ,path = "/signup"
            ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
            ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<SignupUserResponse> createUser(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setUuid(UUID.randomUUID().toString());
        newUserEntity.setFirstName(signupUserRequest.getFirstName());
        newUserEntity.setLastName(signupUserRequest.getLastName());
        newUserEntity.setUserName(signupUserRequest.getUserName());
        newUserEntity.setEmail(signupUserRequest.getEmailAddress());
        newUserEntity.setPassword(signupUserRequest.getPassword());
        newUserEntity.setDob(signupUserRequest.getDob());
        newUserEntity.setContactNumber(signupUserRequest.getContactNumber());
        newUserEntity.setAboutme(signupUserRequest.getAboutMe());
        newUserEntity.setCountry(signupUserRequest.getCountry());
        newUserEntity.setRole("nonadmin");

        UserEntity createdUser = userBusinessService.createUser(newUserEntity);

        SignupUserResponse signupUserResponse = new SignupUserResponse().id(createdUser.getUuid())
                .status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(signupUserResponse,HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.POST, path = "/signin"
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String deocodedText = new String(decode);
        String[] decodedArray = deocodedText.split(":");

        UserAuthenticationEntity existingUser = userBusinessService.authenticateUser(decodedArray[0], decodedArray[1]);

        SigninResponse signinResponse = new SigninResponse()
                .id(existingUser.getUuid())
                .message("SIGNED IN SUCCESSFULLY");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token",existingUser.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse,httpHeaders,HttpStatus.OK);

    }


    @RequestMapping(method = RequestMethod.POST ,
            path = "/signout",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<SignoutResponse> logout(@RequestHeader("authorization") String userAccessToken) throws SignOutRestrictedException {
        UserAuthenticationEntity userAuthenticationEntity = userBusinessService.updateLogoutUser(userAccessToken);

        SignoutResponse signoutResponse = new SignoutResponse()
                .id(userAuthenticationEntity.getUuid())
                .message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse,HttpStatus.OK);

    }


}
