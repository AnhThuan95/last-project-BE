package com.codegym.lastproject.controller;

import com.codegym.lastproject.message.request.EditPasswordForm;
import com.codegym.lastproject.model.Comment;
import com.codegym.lastproject.model.House;
import com.codegym.lastproject.model.Role;
import com.codegym.lastproject.model.User;
import com.codegym.lastproject.security.service.UserDetailsServiceImpl;
import com.codegym.lastproject.service.CommentService;
import com.codegym.lastproject.service.HouseService;
import com.codegym.lastproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin("*")
@RestController
public class UserRestController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUser() {
        User user = userDetailsService.getCurrentUser();
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

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

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile/editPassword")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody EditPasswordForm editPasswordForm) throws Exception {
        User originUser = userDetailsService.getCurrentUser();

        String password = originUser.getPassword();
        System.out.println("1" + password);
        System.out.println("2" + encoder.encode(editPasswordForm.getOldPassword()));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            originUser.getEmail(),
                            editPasswordForm.getOldPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            throw new Exception("Mật khẩu cũ không đúng, vui lòng nhập lại.", e);
        }

        originUser.setPassword(encoder.encode(editPasswordForm.getNewPassword()));
        userService.saveUser(originUser);
        return new ResponseEntity<>(originUser, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/role")
    public ResponseEntity<Role> getRole() {
        User originUser = userDetailsService.getCurrentUser();
        Role role = originUser.getRole().iterator().next();

        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment")
    public ResponseEntity<String> commentHouse(@RequestBody Comment comment) {
        User originUser = userDetailsService.getCurrentUser();
        Comment originComment = new Comment();
        originComment.setUser(originUser);

        Long id = comment.getHouse().getId();
        House originHouse = houseService.findById(id);
        originComment.setHouse(originHouse);

        originComment.setRate(comment.getRate());
        originComment.setComment(comment.getComment());

        commentService.save(originComment);
        return new ResponseEntity<>("Bạn đã đánh giá thành công!", HttpStatus.CREATED);
    }
}
