// package com.example.sms.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import com.example.sms.dto.RoleDto;
// import com.example.sms.entity.Role;
// import com.example.sms.entity.User;
// import com.example.sms.repository.RoleRepository;
// import com.example.sms.repository.UserRepository;
// import com.example.sms.service.RoleService;
// import com.example.sms.service.UserService;

// @Controller
// public class UserRoleController {

// @Autowired
// private UserService userService;

// @Autowired
// private RoleService roleService;

// @Autowired
// private RoleRepository roleRepository;

// @Autowired
// private UserRepository userRepository;

// @GetMapping("/superadmin/users/{userId}/roles")
// public String showUserRoles(@PathVariable("userId") Long userId, Model model)
// {
// User user = userService.findUserById(userId);
// List<RoleDto> allRoles = roleService.ambilDaftarRole();

// model.addAttribute("user", user);
// model.addAttribute("allRoles", allRoles);
// model.addAttribute("currentRoles", user.getRoles());

// return "/superadmin/assignRole";
// }

// @PostMapping("/superadmin/users/{userId}/roles/assign")
// public String assignRoleToUser(
// @PathVariable("userId") Long userId,
// @RequestParam("roleId") Long roleId,
// RedirectAttributes redirectAttributes) {

// try {
// User user = userService.findUserById(userId);
// Role role = roleRepository.findById(roleId).orElseThrow(() ->
// new RuntimeException("Role not found with ID: " + roleId));

// // Check if user already has this role
// boolean hasRole = user.getRoles().stream()
// .anyMatch(r -> r.getId().equals(roleId));

// if (!hasRole) {
// user.getRoles().add(role);
// userRepository.save(user);
// redirectAttributes.addFlashAttribute("success",
// "Role " + role.getName() + " successfully assigned to " + user.getName());
// } else {
// redirectAttributes.addFlashAttribute("info",
// "User already has role " + role.getName());
// }

// } catch (Exception e) {
// redirectAttributes.addFlashAttribute("error",
// "Error assigning role: " + e.getMessage());
// }

// return "redirect:/superadmin/users/" + userId + "/roles";
// }

// @PostMapping("/superadmin/users/{userId}/roles/remove")
// public String removeRoleFromUser(
// @PathVariable("userId") Long userId,
// @RequestParam("roleId") Long roleId,
// RedirectAttributes redirectAttributes) {

// try {
// User user = userService.findUserById(userId);

// // Ensure user has at least one role after removal
// if (user.getRoles().size() <= 1) {
// redirectAttributes.addFlashAttribute("error",
// "Cannot remove the last role. User must have at least one role.");
// return "redirect:/superadmin/users/" + userId + "/roles";
// }

// Role roleToRemove = roleRepository.findById(roleId).orElseThrow(() ->
// new RuntimeException("Role not found with ID: " + roleId));

// boolean removed = user.getRoles().removeIf(r -> r.getId().equals(roleId));

// if (removed) {
// userRepository.save(user);
// redirectAttributes.addFlashAttribute("success",
// "Role " + roleToRemove.getName() + " successfully removed from " +
// user.getName());
// } else {
// redirectAttributes.addFlashAttribute("info",
// "User doesn't have role " + roleToRemove.getName());
// }

// } catch (Exception e) {
// redirectAttributes.addFlashAttribute("error",
// "Error removing role: " + e.getMessage());
// }

// return "redirect:/superadmin/users/" + userId + "/roles";
// }
// }