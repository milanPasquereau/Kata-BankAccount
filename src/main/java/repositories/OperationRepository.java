package repositories;

import model.Operation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OperationRepository {

    Operation create(Operation operation);

    BigDecimal getBalanceOfAccountById(UUID accountId);

    List<Operation> getOperationsOfAccountById(UUID accountId);
}
