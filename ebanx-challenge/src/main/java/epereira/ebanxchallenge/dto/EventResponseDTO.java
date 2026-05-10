package epereira.ebanxchallenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponseDTO {

    public EventResponseDTO(AccountDTO dto, Object o) {
    }
}
