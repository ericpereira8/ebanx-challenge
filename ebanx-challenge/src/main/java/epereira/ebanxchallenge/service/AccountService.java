package epereira.ebanxchallenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import epereira.ebanxchallenge.dto.AccountDTO;
import epereira.ebanxchallenge.dto.EventRequestDTO;
import epereira.ebanxchallenge.dto.EventResponseDTO;
import epereira.ebanxchallenge.entity.Account;
import epereira.ebanxchallenge.exception.ResourceNotFoundException;
import epereira.ebanxchallenge.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Integer getBalance(String accountId) {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return account.getBalance();
    }

    @Transactional
    public EventResponseDTO processEvent(EventRequestDTO event) {
        return switch (event.type()) {
            case "deposit"  -> deposit(event.destination(), event.amount());
            case "withdraw" -> withdraw(event.origin(), event.amount());
            case "transfer" -> transfer(event.origin(), event.destination(), event.amount());
            default -> throw new IllegalArgumentException("Unknown event type: " + event.type());
        };
    }

    @Transactional
    public void reset() {
        repository.deleteAll();
    }

    private EventResponseDTO deposit(String destinationId, Integer amount) {
        Account account = repository.findById(destinationId)
                .orElse(new Account(destinationId, 0));
        account.setBalance(account.getBalance() + amount);
        repository.save(account);
        return new EventResponseDTO(toDTO(account), null);
    }

    private EventResponseDTO withdraw(String originId, Integer amount) {
        Account account = repository.findById(originId)
                .orElseThrow(() -> new ResourceNotFoundException("Origin account not found"));
        account.setBalance(account.getBalance() - amount);
        repository.save(account);
        return new EventResponseDTO(null, toDTO(account));
    }

    private EventResponseDTO transfer(String originId, String destinationId, Integer amount) {
        Account origin = repository.findById(originId)
                .orElseThrow(() -> new ResourceNotFoundException("Origin account not found"));

        Account destination = repository.findById(destinationId)
                .orElse(new Account(destinationId, 0));

        origin.setBalance(origin.getBalance() - amount);
        destination.setBalance(destination.getBalance() + amount);

        repository.save(origin);
        repository.save(destination);

        return new EventResponseDTO(toDTO(destination), toDTO(origin));
    }

    private AccountDTO toDTO(Account account) {
        return new AccountDTO(account.getId(), account.getBalance());
    }
}
