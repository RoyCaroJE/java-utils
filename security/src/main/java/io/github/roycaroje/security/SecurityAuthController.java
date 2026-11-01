package io.github.roycaroje.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityAuthController {

    private final SecurityAccountService accountService;
    private final JwtService jwtService;

    public SecurityAuthController(SecurityAccountService accountService, JwtService jwtService) {
        this.accountService = accountService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        SecurityAccount account = accountService.register(request.username(), request.password());
        String token = jwtService.generateToken(account.getUsername(), accountService.toAuthorities(account));
        return new AuthResponse(token, account.getUsername());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        SecurityAccount account = accountService.authenticate(request.username(), request.password());
        String token = jwtService.generateToken(account.getUsername(), accountService.toAuthorities(account));
        return new AuthResponse(token, account.getUsername());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidation(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    public record AuthRequest(String username, String password) {
    }

    public record AuthResponse(String token, String username) {
    }

    public record ErrorResponse(String error) {
    }
}
