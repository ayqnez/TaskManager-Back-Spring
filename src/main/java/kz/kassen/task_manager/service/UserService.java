package kz.kassen.task_manager.service;

import kz.kassen.task_manager.DTOs.ChangePasswordRequest;
import kz.kassen.task_manager.DTOs.UserDTO;
import kz.kassen.task_manager.model.User;
import kz.kassen.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getPosition());
    }

    public UserDTO updateUser(String email, UserDTO updatedUser) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(updatedUser.getEmail())) {
            Optional<User> existing = userRepository.findByEmail(updatedUser.getEmail());
            if (existing.isPresent() && !existing.get().getUsername().equals(email)) {
                throw new IllegalArgumentException("Email is already in use");
            }
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setPosition(updatedUser.getPosition());

        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getPosition());
    }

    public void changePassword(String email, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}
