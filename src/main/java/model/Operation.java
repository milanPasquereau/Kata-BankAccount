package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Operation(UUID accountId, LocalDateTime dateOperation, BigDecimal amount, OperationType type, BigDecimal newBalance) {
}
