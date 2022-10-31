package writer;

import formatter.FormatterAccountStatement;
import model.AccountStatement;

public class FormattedAccountStatementWriter implements AccountStatementWriter{

    private final FormatterAccountStatement formatter;

    public FormattedAccountStatementWriter(FormatterAccountStatement formatter) {
        this.formatter = formatter;
    }
    @Override
    public void write(AccountStatement accountStatement) {
        System.out.print(formatter.formatAccountStatement(accountStatement));
    }
}