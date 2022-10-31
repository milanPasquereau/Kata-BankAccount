package services;

import exceptions.NegativeAmountException;
import model.Operation;
import model.OperationType;
import repositories.OperationRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class RepositoryBasedOperationService implements OperationService {

    private final OperationRepository operationRepository;

    private final Clock clock;

    public RepositoryBasedOperationService(OperationRepository operationRepository, Clock clock) {
        this.operationRepository = operationRepository;
        this.clock = clock;
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount) throws NegativeAmountException {
        if(amount.doubleValue() <= 0) {
            throw new NegativeAmountException();
        }

        BigDecimal balance = getBalanceOfAccount(accountId);
        createOperation(accountId, amount, OperationType.DEPOSIT, balance.add(amount));
    }

    private BigDecimal getBalanceOfAccount(UUID accountId) {
        return operationRepository.getBalanceOfAccountById(accountId);
    }

    private void createOperation(UUID accountId, BigDecimal amount, OperationType operationType, BigDecimal balance) {
        operationRepository.create(new Operation(accountId, LocalDateTime.now(clock), roundAmount(amount), operationType, roundAmount(balance)));
    }

    private BigDecimal roundAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN);
    }
}
