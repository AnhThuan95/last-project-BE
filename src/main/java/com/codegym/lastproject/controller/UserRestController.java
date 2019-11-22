package com.codegym.lastproject.controller;

import com.codegym.lastproject.model.Role;
import com.codegym.lastproject.model.User;
import com.codegym.lastproject.security.service.UserDetailsServiceImpl;
import com.codegym.lastproject.service.RoleService;
import com.codegym.lastproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin("*")
@Controller
public class UserRestController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    PasswordEncoder encoder;

    @ModelAttribute("role")
    public List<Role> allRole() {
        return roleService.findAll();
    }

//    @GetMapping("/user")
//    public ResponseEntity<List<User>> listAllUsers() {
//        List<User> users = userService.findAll();
//        if (users.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(users, HttpStatus.OK);
//    }

//    @GetMapping(value = "/profile/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HOST')")
//    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
//        System.out.println("Fetching User with id: " + id);
//        User user = userService.findById(id);
//        if (user == null) {
//            System.out.println("User with id " + id + " not found");
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUser() {
        User user = userDetailsService.getCurrentUser();
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


//    @PostMapping(value = "/user/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<Void> createUser(@RequestBody User user) {
//        User originUser = userService.findByEmail(user.getEmail());
//        if (originUser != null) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        System.out.println("Creating User " + user.getName());
//        userService.saveUser(user);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

//    @DeleteMapping("/user/delete/{id}")
//    @ResponseBody
//    public ResponseEntity<Void> apiDeleteUser(@PathVariable("id") Long id) {
//        User target = userService.findById(id);
//
//        if (target == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        userService.remove(target.getId());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

//    @PutMapping(value = "/profile/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HOST')")
//    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
//        User originUser = userService.findById(id);
//
//        if (originUser == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        originUser.setName(user.getName());
//        originUser.setPhone(user.getPhone());
//        originUser.setAddress(user.getAddress());
//        originUser.setAvatar(user.getAvatar());
//
//        userService.saveUser(originUser);
//        return new ResponseEntity<>(originUser, HttpStatus.OK);
//    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile/edit")
    public ResponseEntity<User> updateProfile(@RequestBody User user) {
        User originUser = userDetailsService.getCurrentUser();
        if (user != null) {
            originUser.setName(user.getName());
            originUser.setPhone(user.getPhone());
            originUser.setAddress(user.getAddress());
            originUser.setAvatar(user.getAvatar());

            userService.saveUser(originUser);
            return new ResponseEntity<>(originUser, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

//    @PutMapping(value = "/profile/editPassword/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HOST')")
//    public ResponseEntity<User> editPassword(@PathVariable("id") Long id, @RequestBody User user) {
//        User originUser = userService.findById(id);
//        if (originUser == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        originUser.setPassword(encoder.encode(user.getPassword()));
//
//        userService.saveUser(originUser);
//        return new ResponseEntity<>(originUser, HttpStatus.OK);
//    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile/editPassword")
    public ResponseEntity<User> updatePassword(@RequestBody User user) {
        User originUser = userDetailsService.getCurrentUser();
        if (user != null) {
            originUser.setPassword(encoder.encode(user.getPassword()));

            userService.saveUser(originUser);
            return new ResponseEntity<>(originUser, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/role")
    public ResponseEntity<Role> getRole() {
        User originUser = userDetailsService.getCurrentUser();
        Role role = originUser.getRole().iterator().next();
        System.out.println(role);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
