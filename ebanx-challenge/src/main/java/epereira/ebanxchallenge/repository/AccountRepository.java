package epereira.ebanxchallenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import epereira.ebanxchallenge.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
}
