package epereira.ebanxchallenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import epereira.ebanxchallenge.dto.EventRequestDTO;
import epereira.ebanxchallenge.dto.EventResponseDTO;
import epereira.ebanxchallenge.entity.Account;
import epereira.ebanxchallenge.exception.ResourceNotFoundException;
import epereira.ebanxchallenge.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    // --- getBalance ---

    @Test
    void getBalance_existingAccount_returnsBalance() {
        when(repository.findById("100")).thenReturn(Optional.of(new Account("100", 50)));

        Integer balance = service.getBalance("100");

        assertThat(balance).isEqualTo(50);
    }

    @Test
    void getBalance_nonExistingAccount_throwsResourceNotFoundException() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getBalance("999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- reset ---

    @Test
    void reset_callsDeleteAll() {
        service.reset();

        verify(repository).deleteAll();
    }

    // --- deposit ---

    @Test
    void deposit_newAccount_createsAccountWithAmount() {
        when(repository.findById("100")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventResponseDTO response = service.processEvent(
                new EventRequestDTO("deposit", "100", null, 10));

        assertThat(response.getDestination()).isNotNull();
        assertThat(response.getDestination().id()).isEqualTo("100");
        assertThat(response.getDestination().balance()).isEqualTo(10);
        assertThat(response.getOrigin()).isNull();
    }

    @Test
    void deposit_existingAccount_addsToBalance() {
        when(repository.findById("100")).thenReturn(Optional.of(new Account("100", 20)));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventResponseDTO response = service.processEvent(
                new EventRequestDTO("deposit", "100", null, 10));

        assertThat(response.getDestination().balance()).isEqualTo(30);
    }

    // --- withdraw ---

    @Test
    void withdraw_existingAccount_subtractsFromBalance() {
        when(repository.findById("100")).thenReturn(Optional.of(new Account("100", 20)));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventResponseDTO response = service.processEvent(
                new EventRequestDTO("withdraw", null, "100", 5));

        assertThat(response.getOrigin()).isNotNull();
        assertThat(response.getOrigin().id()).isEqualTo("100");
        assertThat(response.getOrigin().balance()).isEqualTo(15);
        assertThat(response.getDestination()).isNull();
    }

    @Test
    void withdraw_nonExistingAccount_throwsResourceNotFoundException() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.processEvent(
                new EventRequestDTO("withdraw", null, "999", 10)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- transfer ---

    @Test
    void transfer_existingOriginToNewDestination_transfersBalance() {
        when(repository.findById("100")).thenReturn(Optional.of(new Account("100", 50)));
        when(repository.findById("200")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventResponseDTO response = service.processEvent(
                new EventRequestDTO("transfer", "200", "100", 15));

        assertThat(response.getOrigin().id()).isEqualTo("100");
        assertThat(response.getOrigin().balance()).isEqualTo(35);
        assertThat(response.getDestination().id()).isEqualTo("200");
        assertThat(response.getDestination().balance()).isEqualTo(15);
    }

    @Test
    void transfer_nonExistingOrigin_throwsResourceNotFoundException() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.processEvent(
                new EventRequestDTO("transfer", "200", "999", 10)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
