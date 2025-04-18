package com.example.sms.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.sms.dto.UserDto;
import com.example.sms.entity.Role;
import com.example.sms.entity.User;
import com.example.sms.mapper.UserMapper;
import com.example.sms.repository.RoleRepository;
import com.example.sms.repository.UserRepository;
import com.example.sms.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void saveUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        
        user.setPassword(passwordEncoder.encode(userDto.getNip()));

        Role role = roleRepository.findByName("ROLE_USER");
        if(role == null){
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }
    
    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.warn("User belum login atau anonymous");
            return null;
        }

        String currentPrincipalName = authentication.getName();
        logger.info("User login dengan email: {}", currentPrincipalName);

        User user = userRepository.findByEmail(currentPrincipalName);
        if (user == null) {
            logger.error("User dengan email {} tidak ditemukan di database!", currentPrincipalName);
        }

        return user;
    }
    
    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = findUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
        
        // Avoid duplicate roles
        if (user.getRoles().stream().noneMatch(r -> r.getId().equals(roleId))) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }
    
    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = findUserById(userId);
        
        // Ensure user has at least one role after removal
        if (user.getRoles().size() <= 1) {
            throw new RuntimeException("Cannot remove the last role. User must have at least one role.");
        }
        
        boolean removed = user.getRoles().removeIf(role -> role.getId().equals(roleId));
        
        if (removed) {
            userRepository.save(user);
        }
    }
    
    @Override
    public boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }
}