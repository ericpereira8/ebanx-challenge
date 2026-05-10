package epereira.ebanxchallenge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import epereira.ebanxchallenge.dto.EventRequestDTO;
import epereira.ebanxchallenge.dto.EventResponseDTO;
import epereira.ebanxchallenge.exception.ResourceNotFoundException;
import epereira.ebanxchallenge.service.AccountService;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // POST /reset → 200 OK
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        accountService.reset();
        return ResponseEntity.ok("OK");
    }

    // GET /balance?account_id=xxx → 200 <balance> | 404 0
    @GetMapping("/balance")
    public ResponseEntity<Object> getBalance(@RequestParam("account_id") String accountId) {
        try {
            Integer balance = accountService.getBalance(accountId);
            return ResponseEntity.ok(balance);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }
    }

    // POST /event → 201 <response> | 404 0
    @PostMapping("/event")
    public ResponseEntity<Object> handleEvent(@RequestBody EventRequestDTO event) {
        try {
            EventResponseDTO response = accountService.processEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }
    }
}
