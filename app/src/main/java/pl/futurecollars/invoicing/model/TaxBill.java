package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TaxBill {

  private BigDecimal income;
  private BigDecimal costs;
  private BigDecimal incomeMinusCosts;
  private BigDecimal pensionInsurance;
  private BigDecimal incomeMinusCostsMinusPensionInsurance;
  private BigDecimal taxCalculationBase;
  private BigDecimal incomeTax;
  private BigDecimal healthInsuranceFull;
  private BigDecimal healthInsuranceToSubtract;
  private BigDecimal incomeTaxMinusHealthInsurance;
  private BigDecimal finalIncomeTax;
  private BigDecimal collectedVat;
  private BigDecimal paidVat;
  private BigDecimal earnings;
  private BigDecimal vatToPay;

}
