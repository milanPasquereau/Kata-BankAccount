package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AccountStatement(UUID accountId, List<Operation> operations, BigDecimal balance, LocalDateTime date) {
}
