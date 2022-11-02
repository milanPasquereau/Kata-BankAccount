package formatter;

import model.AccountStatement;
import model.Operation;
import model.OperationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountStatementTableFormatterTest {

    @InjectMocks
    private AccountStatementTableFormatter formatter;

    private final Clock clock = Clock.fixed(Instant.parse("2018-04-29T10:15:30.00Z"), ZoneId.of("UTC"));

    @Test
    void shouldFormatAccountStatementWithTwoOperations() {
        final UUID uuid1 = UUID.fromString("0a093538-4ef7-4b4d-b501-1391b03785a3");
        final List<Operation> operations = List.of(
                new Operation(uuid1, LocalDateTime.now(clock), BigDecimal.valueOf(50000.011), OperationType.DEPOSIT, BigDecimal.valueOf(50000.011)),
                new Operation(uuid1, LocalDateTime.now(clock), BigDecimal.valueOf(20000.011), OperationType.WITHDRAW, BigDecimal.valueOf(30000.011))
        );
        final String resultFormatted = formatter.formatAccountStatement(new AccountStatement(uuid1, operations, BigDecimal.valueOf(30000.011), LocalDateTime.now(clock)));
        final String resultExpected = """
                +-------------------------------------------------------------------------+
                |                            Account Statement                            |
                +-------------------------------------------------------------------------+
                | ID: 0a093538-4ef7-4b4d-b501-1391b03785a3     | Date: 29/04/2018 10:15   |
                +-------------------------------------------------------------------------+
                |       Date       |   Type   |       Amount        |     New Balance     |
                +-------------------------------------------------------------------------+
                | 29/04/2018 10:15 | DEPOSIT  | $ 50000,01          | $ 50000,01          |"""
                                        + System.lineSeparator() +                         """
                | 29/04/2018 10:15 | WITHDRAW | $ 20000,01          | $ 30000,01          |"""
                                        + System.lineSeparator() +                         """
                +-------------------------------------------------------------------------+
                | Balance: $ 30000,01                                                     |
                +-------------------------------------------------------------------------+""";

        assertEquals(resultExpected, resultFormatted);
    }

    @Test
    void shouldFormatAccountStatementWithoutOperations() {
        final UUID uuid1 = UUID.fromString("0a093538-4ef7-4b4d-b501-1391b03785a3");
        final List<Operation> operations = List.of();
        final String resultFormatted = formatter.formatAccountStatement(new AccountStatement(uuid1, operations, BigDecimal.valueOf(30000.011), LocalDateTime.now(clock)));
        final String resultExpected = """
                +-------------------------------------------------------------------------+
                |                            Account Statement                            |
                +-------------------------------------------------------------------------+
                | ID: 0a093538-4ef7-4b4d-b501-1391b03785a3     | Date: 29/04/2018 10:15   |
                +-------------------------------------------------------------------------+
                |       Date       |   Type   |       Amount        |     New Balance     |
                +-------------------------------------------------------------------------+
                | No operations                                                           |"""
                                        + System.lineSeparator() +                         """
                +-------------------------------------------------------------------------+
                | Balance: $ 30000,01                                                     |
                +-------------------------------------------------------------------------+""";

        assertEquals(resultExpected, resultFormatted);
    }
}