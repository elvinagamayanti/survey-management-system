/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sms.mapper;

import com.example.sms.dto.UserDto;
import com.example.sms.entity.User;

/**
 *
 * @author pinaa
 */
public class UserMapper {
    // map User entity to User Dto
    public static UserDto mapToUserDto(User user) {
        // Membuat dto dengan builder pattern (inject dari lombok)
        String[] str = user.getName().split(" ");
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .firstName(str[0])
                .lastName(str[1])
                .nip(user.getNip())
                .email(user.getEmail())
                .satker(user.getSatker())
                .build();        
        return userDto;
    }    
    // map User Dto ke User Entity
    public static User mapToUser(UserDto userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .name(userDto.getFirstName() + " " + userDto.getLastName())
                .nip(userDto.getNip())
                .email(userDto.getEmail())
                .satker(userDto.getSatker())
                .build();        
        return user;
    }
}
