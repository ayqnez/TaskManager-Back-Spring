package kz.kassen.task_manager.DTOs;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String email;
    private String password;
}
