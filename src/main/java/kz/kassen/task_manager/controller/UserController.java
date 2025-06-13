package kz.kassen.task_manager.controller;

import kz.kassen.task_manager.DTOs.ChangePasswordRequest;
import kz.kassen.task_manager.DTOs.UserDTO;
import kz.kassen.task_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserByEmail(Authentication auth) {
        return ResponseEntity.ok(userService.getUserByEmail(auth.getName()));
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(Authentication auth, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(auth.getName(), userDTO));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication auth, @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(auth.getName(), changePasswordRequest);
        return ResponseEntity.ok().build();
    }
}
