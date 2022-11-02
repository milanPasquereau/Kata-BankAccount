package services;

import exceptions.InsufficientBalanceException;
import exceptions.NegativeAmountException;
import model.AccountStatement;
import model.Operation;
import model.OperationType;
import repositories.OperationRepository;
import writer.AccountStatementWriter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RepositoryBasedOperationService implements OperationService {

    private final OperationRepository operationRepository;

    private final Clock clock;

    private final AccountStatementWriter writer;

    public RepositoryBasedOperationService(OperationRepository operationRepository, Clock clock, AccountStatementWriter writer) {
        this.operationRepository = operationRepository;
        this.clock = clock;
        this.writer = writer;
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount) throws NegativeAmountException {
        if(amount.doubleValue() <= 0) {
            throw new NegativeAmountException();
        }
        amount = roundAmount(amount);
        BigDecimal balance = getBalanceOfAccount(accountId);
        createOperation(accountId, amount, OperationType.DEPOSIT, balance.add(amount));
    }

    @Override
    public void withdraw(UUID accountId, BigDecimal amount) throws NegativeAmountException, InsufficientBalanceException {
        if(amount.doubleValue() < 0) {
            throw new NegativeAmountException();
        }

        BigDecimal balance = getBalanceOfAccount(accountId);
        amount = roundAmount(amount);
        if(balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        createOperation(accountId, amount, OperationType.WITHDRAW, balance.subtract(amount));
    }

    @Override
    public void printAccountStatement(UUID accountId) {
        BigDecimal balance = getBalanceOfAccount(accountId);
        List<Operation> operations = operationRepository.getOperationsOfAccountById(accountId);
        writer.write(new AccountStatement(accountId, operations, balance, LocalDateTime.now(clock)));
    }

    private BigDecimal getBalanceOfAccount(UUID accountId) {
        return operationRepository.getBalanceOfAccountById(accountId);
    }

    private void createOperation(UUID accountId, BigDecimal amount, OperationType operationType, BigDecimal balance) {
        operationRepository.create(new Operation(accountId, LocalDateTime.now(clock), amount, operationType, balance));
    }

    private BigDecimal roundAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN);
    }
}
