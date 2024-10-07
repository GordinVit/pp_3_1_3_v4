package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.*;

@Controller
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public String getAllUser(@AuthenticationPrincipal User activeUser,Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("user", new User());
        model.addAttribute("roles", activeUser.getRoleSet());
        return "users";
    }

    @GetMapping(value = "/user/{id}")
    public String getUserById(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping(value = "/createUser")
    public String createUser(@ModelAttribute("user") User user, @RequestParam ArrayList<String> listRoleId, Model model) {
        if (!userService.isUsernameUnique(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists!");
            model.addAttribute("users", userService.getAllUsers());
            return "users";
        }

        Set<Role> userRole = new HashSet<>();
        for (String roleId : listRoleId) {
            Role role = roleService.getRoleById(Long.parseLong(roleId));
            userRole.add(role);
        }
        user.setRoleSet(userRole);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PatchMapping(value = "/updateUser/{id}")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam ArrayList<String> listRoleId, Model model) {
        User existingUser = userService.getUserById(user.getId());

        if (!existingUser.getUsername().equals(user.getUsername()) && !userService.isUsernameUnique(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists!");
            model.addAttribute("users", userService.getAllUsers());
            return "users";
        }

        Set<Role> userRole = new HashSet<>();
        for (String roleId : listRoleId) {
            Role role = roleService.getRoleById(Long.parseLong(roleId));
            userRole.add(role);
        }
        user.setRoleSet(userRole);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @DeleteMapping(value = "/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
    @GetMapping(value = "/user")
    public String oneUser(@AuthenticationPrincipal User activeUser, Model model) {
        model.addAttribute("roles", activeUser.getRoleSet());
        model.addAttribute("user", activeUser);
        return "user";
    }
}