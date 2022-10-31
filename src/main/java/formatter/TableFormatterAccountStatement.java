package formatter;

import model.AccountStatement;
import model.Operation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;

public class TableFormatterAccountStatement implements FormatterAccountStatement {

    private static final String FORMAT_DATE = "dd/MM/yyyy HH:mm";

    private static final String OPERATION_FORMAT = "| %-15s | %-8s | $ %-17s | $ %-17s |";

    private static final String NO_OPERATION_FORMAT = "| %-71s |";

    private static final String BALANCE_FORMAT = "| Balance: $ %-60s |";

    private static final int NB_DIGITS_BIGDECIMAL = 2;

    private static final boolean GROUPING_USED = false;

    private final DecimalFormat df = new DecimalFormat();

    public TableFormatterAccountStatement() {
        df.setMaximumFractionDigits(NB_DIGITS_BIGDECIMAL);
        df.setGroupingUsed(GROUPING_USED);
    }

    @Override
    public String formatAccountStatement(AccountStatement accountStatement) {
        List<Operation> operations = accountStatement.operations();
        String renderedOperations = "";


        if(operations.isEmpty()) {
            renderedOperations = format(NO_OPERATION_FORMAT, "No operations").concat(System.lineSeparator());
        }

        for (Operation operation : operations) {
            renderedOperations = renderedOperations.concat(format(
                    OPERATION_FORMAT,
                    formatDate(operation.dateOperation()),
                    operation.type(),
                    roundBigDecimal(operation.amount()),
                    roundBigDecimal(operation.newBalance()))) + System.lineSeparator();
        }

        return """
                +-------------------------------------------------------------------------+
                |                            Account Statement                            |
                +-------------------------------------------------------------------------+
                | ID: %s     | Date: %s   |
                +-------------------------------------------------------------------------+
                |       Date       |   Type   |       Amount        |     New Balance     |
                +-------------------------------------------------------------------------+
                %s+-------------------------------------------------------------------------+
                %s
                +-------------------------------------------------------------------------+""".formatted(
                accountStatement.accountId(),
                formatDate(accountStatement.date()),
                renderedOperations,
                format(BALANCE_FORMAT,  roundBigDecimal(accountStatement.balance())));
    }

    private String formatDate(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(FORMAT_DATE).format(localDateTime);
    }

    private String roundBigDecimal(BigDecimal amount) {
        return df.format(amount);
    }
}