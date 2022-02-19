package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceEntry {

  private final String description;
  private final BigDecimal price;
  private final BigDecimal vatValue;
  private final Vat vatRate;

}
