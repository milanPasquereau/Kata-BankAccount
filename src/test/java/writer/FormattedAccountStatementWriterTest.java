package writer;

import formatter.FormatterAccountStatement;
import model.AccountStatement;
import model.Operation;
import model.OperationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormattedAccountStatementWriterTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;

    @Mock
    private FormatterAccountStatement formatter;

    @InjectMocks
    private FormattedAccountStatementWriter writer;

    private final Clock clock = Clock.fixed(Instant.parse("2018-04-29T10:15:30.00Z"), ZoneId.of("UTC"));

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void shouldPrintAccountStatementWithOperations() {
        final UUID uuid1 = UUID.fromString("0a093538-4ef7-4b4d-b501-1391b03785a3");
        final List<Operation> operations = List.of(
                new Operation(uuid1, LocalDateTime.now(clock), BigDecimal.valueOf(50000.011), OperationType.DEPOSIT, BigDecimal.valueOf(50000.011)),
                new Operation(uuid1, LocalDateTime.now(clock), BigDecimal.valueOf(20000.011), OperationType.WITHDRAW, BigDecimal.valueOf(30000.011))
        );
        final AccountStatement statement = new AccountStatement(uuid1, operations, BigDecimal.valueOf(30000.011), LocalDateTime.now(clock));

        final String resultExpected = """
               Hello
               World""";
        when(formatter.formatAccountStatement(statement)).thenReturn(resultExpected);

        writer.write(statement);

        assertEquals(resultExpected, outContent.toString());
        verify(formatter).formatAccountStatement(statement);
        verifyNoMoreInteractions(formatter);
    }
}