package epereira.ebanxchallenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class EventResponseDTO {

    private final AccountDTO destination;
    private final AccountDTO origin;

    public EventResponseDTO(AccountDTO destination, AccountDTO origin) {
        this.destination = destination;
        this.origin = origin;
    }
}
