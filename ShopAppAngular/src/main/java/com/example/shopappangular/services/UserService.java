package com.example.shopappangular.services;

import com.example.shopappangular.dtos.UpdateUserDTO;
import com.example.shopappangular.dtos.UserDTO;
import com.example.shopappangular.models.User;

public interface UserService {

    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password, Long role) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;


}
