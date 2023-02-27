package com.osayijoy.swiftwallet.service;


import com.osayijoy.swiftwallet.dtos.request.UpdateUserRequestDto;
import com.osayijoy.swiftwallet.dtos.request.UserRegistrationRequestDto;
import com.osayijoy.swiftwallet.dtos.response.RegistrationResponseDto;
import com.osayijoy.swiftwallet.entity.User;

public interface UserService {
    RegistrationResponseDto registerUser(UserRegistrationRequestDto registrationRequestDto);
    User getLoggedInUser();
    void updateUser(UpdateUserRequestDto updateUserDto, String id);
    String verifyAccount(String token);
}
