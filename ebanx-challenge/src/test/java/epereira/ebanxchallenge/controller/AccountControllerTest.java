package epereira.ebanxchallenge.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import epereira.ebanxchallenge.dto.AccountDTO;
import epereira.ebanxchallenge.dto.EventRequestDTO;
import epereira.ebanxchallenge.dto.EventResponseDTO;
import epereira.ebanxchallenge.exception.ResourceNotFoundException;
import epereira.ebanxchallenge.service.AccountService;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController controller;

    // --- POST /reset ---

    @Test
    void reset_returns200OK() {
        doNothing().when(accountService).reset();

        ResponseEntity<String> response = controller.reset();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("OK");
        verify(accountService).reset();
    }

    // --- GET /balance ---

    @Test
    void getBalance_existingAccount_returns200WithBalance() {
        when(accountService.getBalance("100")).thenReturn(20);

        ResponseEntity<Object> response = controller.getBalance("100");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(20);
    }

    @Test
    void getBalance_nonExistingAccount_returns404With0() {
        when(accountService.getBalance("999"))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        ResponseEntity<Object> response = controller.getBalance("999");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(0);
    }

    // --- POST /event deposit ---

    @Test
    void event_deposit_newAccount_returns201WithDestination() {
        EventResponseDTO dto = new EventResponseDTO(new AccountDTO("100", 10), null);
        when(accountService.processEvent(any())).thenReturn(dto);

        ResponseEntity<Object> response = controller.handleEvent(
                new EventRequestDTO("deposit", "100", null, 10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventResponseDTO body = (EventResponseDTO) response.getBody();
        assertThat(body.getDestination().id()).isEqualTo("100");
        assertThat(body.getDestination().balance()).isEqualTo(10);
        assertThat(body.getOrigin()).isNull();
    }

    // --- POST /event withdraw ---

    @Test
    void event_withdraw_existingAccount_returns201WithOrigin() {
        EventResponseDTO dto = new EventResponseDTO(null, new AccountDTO("100", 15));
        when(accountService.processEvent(any())).thenReturn(dto);

        ResponseEntity<Object> response = controller.handleEvent(
                new EventRequestDTO("withdraw", null, "100", 5));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventResponseDTO body = (EventResponseDTO) response.getBody();
        assertThat(body.getOrigin().id()).isEqualTo("100");
        assertThat(body.getOrigin().balance()).isEqualTo(15);
        assertThat(body.getDestination()).isNull();
    }

    @Test
    void event_withdraw_nonExistingAccount_returns404With0() {
        when(accountService.processEvent(any()))
                .thenThrow(new ResourceNotFoundException("Origin account not found"));

        ResponseEntity<Object> response = controller.handleEvent(
                new EventRequestDTO("withdraw", null, "999", 5));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(0);
    }

    // --- POST /event transfer ---

    @Test
    void event_transfer_returns201WithOriginAndDestination() {
        EventResponseDTO dto = new EventResponseDTO(
                new AccountDTO("200", 15),
                new AccountDTO("100", 35));
        when(accountService.processEvent(any())).thenReturn(dto);

        ResponseEntity<Object> response = controller.handleEvent(
                new EventRequestDTO("transfer", "200", "100", 15));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventResponseDTO body = (EventResponseDTO) response.getBody();
        assertThat(body.getOrigin().id()).isEqualTo("100");
        assertThat(body.getOrigin().balance()).isEqualTo(35);
        assertThat(body.getDestination().id()).isEqualTo("200");
        assertThat(body.getDestination().balance()).isEqualTo(15);
    }

    @Test
    void event_transfer_nonExistingOrigin_returns404With0() {
        when(accountService.processEvent(any()))
                .thenThrow(new ResourceNotFoundException("Origin account not found"));

        ResponseEntity<Object> response = controller.handleEvent(
                new EventRequestDTO("transfer", "200", "999", 15));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(0);
    }
}
