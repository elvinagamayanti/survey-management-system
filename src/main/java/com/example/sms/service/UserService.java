/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.sms.service;

import com.example.sms.dto.UserDto;
import com.example.sms.entity.User;
import java.util.List;

/**
 *
 * @author pinaa
 */
public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
    
    User findUserById(Long id);

    User getCurrentUser();

    User getUserLogged();
}
