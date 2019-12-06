package com.codegym.lastproject.controller;

import com.codegym.lastproject.message.request.LoginForm;
import com.codegym.lastproject.message.request.SignUpForm;
import com.codegym.lastproject.message.response.JwtResponse;
import com.codegym.lastproject.model.Role;
import com.codegym.lastproject.model.util.RoleName;
import com.codegym.lastproject.model.User;
import com.codegym.lastproject.security.jwt.JwtProvider;
import com.codegym.lastproject.service.RoleService;
import com.codegym.lastproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginForm loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if(userService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>("Email đã tồn tại, vui lòng dùng email khác. Nếu bạn đã có tài khoản, đăng nhập để tiếp tục.", HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch(role) {
                case "admin":
                    Role adminRole = roleService.findByName(RoleName.ROLE_ADMIN);
                    if (adminRole == null) {
                        throw new UsernameNotFoundException("Fail! -> Cause: User Role not find.");
                    }
                    roles.add(adminRole);
                    break;

                case "host":
                    Role hostRole = roleService.findByName(RoleName.ROLE_HOST);
                    if (hostRole == null) {
                        throw new UsernameNotFoundException("Fail! -> Cause: User Role not find.");
                    }
                    roles.add(hostRole);
                    break;

                default:
                    Role userRole = roleService.findByName(RoleName.ROLE_USER);
                    if (userRole == null) {
                        throw new UsernameNotFoundException("Fail! -> Cause: User Role not find.");
                    }
                    roles.add(userRole);
            }
        });

        user.setRole(roles);
        userService.saveUser(user);

        return new ResponseEntity<>("Tạo tài khoản thành công, vui lòng đăng nhập để tiếp tục.", HttpStatus.CREATED);
    }
}
