package kz.kassen.task_manager.service;

import kz.kassen.task_manager.DTOs.*;
import kz.kassen.task_manager.config.JwtUtils;
import kz.kassen.task_manager.model.RefreshToken;
import kz.kassen.task_manager.model.User;
import kz.kassen.task_manager.repository.RefreshTokenRepository;
import kz.kassen.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshTokenString = jwtUtils.generateRefreshToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        saveRefreshToken(refreshTokenString, user);

        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getPosition());

        return new AuthResponse(accessToken, refreshTokenString, userDTO);
    }

    protected void saveRefreshToken(String token, User user) {
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .expiryDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 дней
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public String register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Пользователь уже существует");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        userRepository.save(user);
        return "Регистрация прошла успешно";
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUser().getEmail());
        String newAccessToken = jwtUtils.generateToken(userDetails);
        String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

        saveRefreshToken(newRefreshToken, refreshToken.getUser());

        User user = refreshToken.getUser();
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getPosition());

        return new AuthResponse(newAccessToken, newRefreshToken, userDTO);
    }

    @Transactional
    public void deleteRefreshToken(String refreshTokenString) {
        refreshTokenRepository.deleteByToken(refreshTokenString);
    }
}
