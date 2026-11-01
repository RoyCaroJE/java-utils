package io.github.roycaroje.security;

import io.github.roycaroje.security.dto.AccountProfileResponse;
import io.github.roycaroje.security.dto.ChangePasswordRequest;
import io.github.roycaroje.security.dto.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public AccountProfileResponse me() {
        return accountService.me();
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(request);
        return new MessageResponse("Password changed");
    }

    @PostMapping("/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse deactivateOwnAccount() {
        accountService.deactivateOwnAccount();
        return new MessageResponse("Account deactivated");
    }
}
