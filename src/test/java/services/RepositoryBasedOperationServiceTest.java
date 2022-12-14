package services;

import exceptions.InsufficientBalanceException;
import exceptions.NegativeAmountException;
import model.AccountStatement;
import model.Operation;
import model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repositories.OperationRepository;
import writer.AccountStatementWriter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryBasedOperationServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private AccountStatementWriter writer;

    @InjectMocks
    private RepositoryBasedOperationService operationService;

    private final Clock clock = Clock.fixed(Instant.parse("2018-04-29T10:15:30.00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void init() {
        operationService = new RepositoryBasedOperationService(operationRepository, clock, writer);
    }

    @Test
    void shouldDepositValidAmountOnAccount() throws NegativeAmountException {
        final UUID accountId = UUID.randomUUID();
        final BigDecimal balance = BigDecimal.valueOf(20000).setScale(2, RoundingMode.HALF_EVEN);
        final BigDecimal deposit = BigDecimal.valueOf(10000).setScale(2, RoundingMode.HALF_EVEN);

        final Operation expectedOp = new Operation(accountId, LocalDateTime.now(clock), deposit, OperationType.DEPOSIT, balance.add(deposit));
        when(operationRepository.getBalanceOfAccountById(accountId)).thenReturn(balance);

        operationService.deposit(accountId, deposit);

        InOrder inOrder = inOrder(operationRepository);
        inOrder.verify(operationRepository).getBalanceOfAccountById(accountId);
        inOrder.verify(operationRepository).create(expectedOp);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotDepositInvalidAmountAndReturnNegativeAmountException() {
        final UUID accountId = UUID.randomUUID();
        assertThrows(NegativeAmountException.class, () -> operationService.deposit(accountId, BigDecimal.valueOf(-10000)));

        verifyNoInteractions(operationRepository);
    }

    @Test
    void shouldWithdrawValidAmountFromAccount() throws NegativeAmountException, InsufficientBalanceException {
        final UUID accountId = UUID.randomUUID();
        final BigDecimal balance = BigDecimal.valueOf(20000).setScale(2, RoundingMode.HALF_EVEN);
        final BigDecimal withdraw = BigDecimal.valueOf(10000).setScale(2, RoundingMode.HALF_EVEN);

        final Operation expectedOp  = new Operation(accountId, LocalDateTime.now(clock), withdraw, OperationType.WITHDRAW, balance.subtract(withdraw));
        when(operationRepository.getBalanceOfAccountById(accountId)).thenReturn(balance);
        when(operationRepository.create(any())).thenReturn(expectedOp);

        operationService.withdraw(accountId, withdraw);

        InOrder inOrder = inOrder(operationRepository);
        inOrder.verify(operationRepository).getBalanceOfAccountById(accountId);
        inOrder.verify(operationRepository).create(expectedOp);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotWithdrawInvalidAmountFromAccountAndReturnNegativeAmountException() {
        final UUID accountId = UUID.randomUUID();
        final BigDecimal withdraw = BigDecimal.valueOf(-10000);
        assertThrows(NegativeAmountException.class, () -> operationService.withdraw(accountId, withdraw));

        verifyNoInteractions(operationRepository);
    }

    @Test
    void shouldNotWithdrawInvalidAmountFromAccountAndReturnInsufficientBalanceException() {
        final UUID accountId = UUID.randomUUID();
        final BigDecimal balance = BigDecimal.valueOf(2000);
        final BigDecimal withdraw = BigDecimal.valueOf(10000);
        when(operationRepository.getBalanceOfAccountById(accountId)).thenReturn(balance);

        assertThrows(InsufficientBalanceException.class, () -> operationService.withdraw(accountId, withdraw));

        verify(operationRepository).getBalanceOfAccountById(accountId);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void shouldPrintAccountStatement() {
        final UUID accountId = UUID.randomUUID();
        final BigDecimal balance = BigDecimal.valueOf(30000.011);
        final List<Operation> operations = List.of(
                new Operation(accountId, LocalDateTime.now(clock), BigDecimal.valueOf(50000.011), OperationType.DEPOSIT, BigDecimal.valueOf(50000.011)),
                new Operation(accountId, LocalDateTime.now(clock), BigDecimal.valueOf(20000.011), OperationType.WITHDRAW, BigDecimal.valueOf(30000.011))
        );
        final AccountStatement statement = new AccountStatement(accountId, operations, BigDecimal.valueOf(30000.011), LocalDateTime.now(clock));
        when(operationRepository.getBalanceOfAccountById(accountId)).thenReturn(balance);
        when(operationRepository.getOperationsOfAccountById(accountId)).thenReturn(operations);

        operationService.printAccountStatement(accountId);

        InOrder inOrder = inOrder(operationRepository, writer);
        inOrder.verify(operationRepository).getBalanceOfAccountById(accountId);
        inOrder.verify(operationRepository).getOperationsOfAccountById(accountId);
        inOrder.verify(writer).write(statement);
        inOrder.verifyNoMoreInteractions();
    }
}