package epereira.ebanxchallenge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

    @Id
    private String id;

    @Column(nullable = false)
    private Integer balance;

    public Account() {
    }

    public Account(String id, Integer balance) {
        this.id = id;
        this.balance = balance;
    }
}
