package services;

import exceptions.NegativeAmountException;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryBasedOperationServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private RepositoryBasedOperationService operationService;

    private final Clock clock = Clock.fixed(Instant.parse("2018-04-29T10:15:30.00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void init() {
        operationService = new RepositoryBasedOperationService(operationRepository, clock);
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
}