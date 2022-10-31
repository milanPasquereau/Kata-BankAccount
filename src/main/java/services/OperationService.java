package services;

import exceptions.InsufficientBalanceException;
import exceptions.NegativeAmountException;

import java.math.BigDecimal;
import java.util.UUID;

public interface OperationService {

    void deposit(UUID id, BigDecimal amount) throws NegativeAmountException;

    void withdraw(UUID id, BigDecimal amount) throws NegativeAmountException, InsufficientBalanceException;
}
