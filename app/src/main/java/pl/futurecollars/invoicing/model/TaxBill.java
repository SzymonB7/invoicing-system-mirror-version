package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class TaxBill {

  private final BigDecimal income;
  private final BigDecimal costs;
  private final BigDecimal incomingVat;
  private final BigDecimal outgoingVat;
  private final BigDecimal earnings;
  private final BigDecimal vatToPay;

}

