package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommonController {

    @Autowired
    UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.GET ,
            path ="/userprofile/{userId}",
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable(name = "userId") String userId,
                                                       @RequestHeader("authorization") String authorization)
            throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = userBusinessService.userProfile(userId, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().aboutMe(userEntity.getAboutme())
                .country(userEntity.getCountry())
                .dob(userEntity.getDob())
                .contactNumber(userEntity.getContactNumber())
                .emailAddress(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUserName());

   return   new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);


    }
}
