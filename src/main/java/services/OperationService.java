package services;

import exceptions.NegativeAmountException;

import java.math.BigDecimal;
import java.util.UUID;

public interface OperationService {

    void deposit(UUID id, BigDecimal amount) throws NegativeAmountException;
}
