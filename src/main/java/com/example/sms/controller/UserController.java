// /*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
// to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit
// this template
// */
// package com.example.sms.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.example.sms.dto.UserDto;
// import com.example.sms.entity.Satker;
// import com.example.sms.entity.User;
// import com.example.sms.repository.SatkerRepository;
// import com.example.sms.repository.UserRepository;
// import com.example.sms.service.UserService;

// import jakarta.validation.Valid;

// /**
// *
// * @author pinaa
// */
// @Controller
// public class UserController {

// private UserService userService;

// @Autowired
// private SatkerRepository satkerRepository;
// @Autowired
// private UserRepository userRepository;

// public UserController(UserService userService) {
// this.userService = userService;
// }

// // handler method to handle home page request
// @GetMapping("/index")
// public String home(Model model){
// String username = ((UserDetails)
// SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
// User user = userRepository.findByEmail(username);
// if (user == null) {
// throw new RuntimeException("User not found");
// }
// model.addAttribute("user", user);
// return "redirect:/";
// }

// @GetMapping("/navbar")
// public String navbar(Model model){
// String username = ((UserDetails)
// SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
// User user = userRepository.findByEmail(username);
// if (user == null) {
// throw new RuntimeException("User not found");
// }
// model.addAttribute("user", user);
// return "/layout/navbar";
// }

// // handler method to handle login request
// @GetMapping("/login")
// public String login(){
// return "login";
// }

// // handler method to handle user registration form request
// @GetMapping("/superadmin/users/add")
// public String showRegistrationForm(Model model){
// // create model object to store form data
// UserDto user = new UserDto();
// List<Satker> listSatkers = satkerRepository.findAll();
// model.addAttribute("user", user);
// model.addAttribute("listSatkers", listSatkers);
// return "/admin/addPengguna";
// }

// // handler method to handle user registration form submit request
// @PostMapping("/superadmin/users/save")
// public String registration(@Valid @ModelAttribute("user") UserDto userDto,
// BindingResult result,
// Model model){
// User existingUser = userService.findUserByEmail(userDto.getEmail());

// if(existingUser != null && existingUser.getEmail() != null &&
// !existingUser.getEmail().isEmpty()){
// result.rejectValue("email", null,
// "There is already an account registered with the same email");
// }

// if(result.hasErrors()){
// model.addAttribute("user", userDto);
// return "/admin/addPengguna";
// }

// userService.saveUser(userDto);
// return "redirect:/superadmin/users";
// }

// // handler method to handle list of users
// @GetMapping("/superadmin/users")
// public String users(Model model){
// List<UserDto> users = userService.findAllUsers();
// model.addAttribute("users", users);
// return "/admin/manajemenPengguna";
// }

// @GetMapping("/superadmin/users/detail")
// public String viewUserDetail(@RequestParam("id") Long id, Model model) {
// User user = userService.findUserById(id);
// model.addAttribute("user", user);
// return "/admin/detailPengguna";
// }

// // @GetMapping("/navbar")
// // public String navbar(Model model) {
// // Authentication authentication =
// SecurityContextHolder.getContext().getAuthentication();
// // if (authentication != null && authentication.getPrincipal() instanceof
// UserDetails) {
// // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
// // User user = userService.findUserByEmail(userDetails.getUsername());
// // model.addAttribute("currentUser", user);
// // }
// // return "layout/navbar";
// // }

// @ModelAttribute("currentUser")
// public User getCurrentUser() {
// String email =
// SecurityContextHolder.getContext().getAuthentication().getName();
// User currentUser = userService.findUserByEmail(email);
// return currentUser;
// }

// @GetMapping("/user/detail")
// public String viewProfileDetail(Model model) {
// String username = ((UserDetails)
// SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
// User user = userRepository.findByEmail(username);
// if (user == null) {
// throw new RuntimeException("User not found");
// }
// model.addAttribute("user", user);
// return "/detailProfil";
// }

// }