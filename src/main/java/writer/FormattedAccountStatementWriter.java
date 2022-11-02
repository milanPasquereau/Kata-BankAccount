package writer;

import formatter.AccountStatementFormatter;
import model.AccountStatement;

public class FormattedAccountStatementWriter implements AccountStatementWriter{

    private final AccountStatementFormatter formatter;

    public FormattedAccountStatementWriter(AccountStatementFormatter formatter) {
        this.formatter = formatter;
    }
    @Override
    public void write(AccountStatement accountStatement) {
        System.out.print(formatter.formatAccountStatement(accountStatement));
    }
}