package com.mediatica.onlinebanking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DashboardDTO {

    private int accountsNumber;
    private int transactionsNumber;
    private int cardsNumber;
    private BigDecimal totalBalance;
    private BigDecimal totalIncoming;
    private BigDecimal totalOutcoming;
}
