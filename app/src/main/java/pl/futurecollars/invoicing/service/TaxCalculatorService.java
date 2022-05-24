package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.TaxBill;

@Service
@AllArgsConstructor
public class TaxCalculatorService {
  private final Database database;

  public Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
    return invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxIdentificationNumber);
  }

  public Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
    return invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxIdentificationNumber);
  }

  public BigDecimal calculateVatValue(InvoiceEntry invoiceEntry) {
    if (invoiceEntry.getCarExpenseIsRelatedTo() != null && invoiceEntry.getCarExpenseIsRelatedTo().isUsedForPersonalPurpose()) {
      return invoiceEntry.getVatValue().divide(BigDecimal.valueOf(2), 2, RoundingMode.FLOOR);
    }
    return invoiceEntry.getVatValue();
  }

  public BigDecimal calculateCompanyCostsConsideringPersonalCarUsage(InvoiceEntry invoiceEntry) {
    return invoiceEntry.getNetPrice().add(invoiceEntry.getVatValue()).subtract(calculateVatValue(invoiceEntry));
  }

  public BigDecimal calculateCollectedVat(String taxIdentificationNumber) {
    return database.visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal calculatePaidVat(String taxIdentificationNumber) {
    return database.visit(buyerPredicate(taxIdentificationNumber), invoiceEntry -> calculateVatValue(invoiceEntry));
  }

  public BigDecimal calculateIncome(String taxIdentificationNumber) {
    return database.visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getNetPrice);
  }

  public BigDecimal calculateCosts(String taxIdentificationNumber) {
    return database.visit(buyerPredicate(taxIdentificationNumber), invoiceEntry -> calculateCompanyCostsConsideringPersonalCarUsage(invoiceEntry));
  }

  public BigDecimal calculateEarnings(String taxIdentificationNumber) {
    return calculateIncome(taxIdentificationNumber).subtract(calculateCosts(taxIdentificationNumber));
  }

  public BigDecimal calculateVatToPay(String taxIdentificationNumber) {
    return calculateCollectedVat(taxIdentificationNumber).subtract(calculatePaidVat(taxIdentificationNumber));
  }

  public TaxBill calculateTaxes(Company company) {

    String taxIdentificationNumber = company.getTaxIdentificationNumber();
    BigDecimal incomeMinusCostsMinusPensionInsurance = calculateEarnings(taxIdentificationNumber).subtract(company.getPensionInsurance());
    BigDecimal taxCalculationBase = incomeMinusCostsMinusPensionInsurance.setScale(0, RoundingMode.HALF_DOWN);
    BigDecimal incomeTax = taxCalculationBase.multiply(BigDecimal.valueOf(19, 2));
    BigDecimal healthInsuranceToSubtract = company.getHealthInsurance().multiply(BigDecimal.valueOf(100))
        .divide(BigDecimal.valueOf(9), RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(775, 4))
        .setScale(2, RoundingMode.HALF_UP);

    return TaxBill.builder()
        .income(calculateIncome(taxIdentificationNumber))
        .costs(calculateCosts(taxIdentificationNumber))
        .incomeMinusCosts(calculateEarnings(taxIdentificationNumber))
        .pensionInsurance(company.getPensionInsurance())
        .incomeMinusCostsMinusPensionInsurance(incomeMinusCostsMinusPensionInsurance)
        .taxCalculationBase(taxCalculationBase)
        .incomeTax(taxCalculationBase.multiply(BigDecimal.valueOf(19, 2)))
        .healthInsuranceFull(company.getHealthInsurance())
        .healthInsuranceToSubtract(healthInsuranceToSubtract)
        .incomeTaxMinusHealthInsurance(incomeTax.subtract(healthInsuranceToSubtract))
        .finalIncomeTax(incomeTax.subtract(healthInsuranceToSubtract).setScale(0, RoundingMode.DOWN))

        .collectedVat(calculateCollectedVat(taxIdentificationNumber))
        .paidVat(calculatePaidVat(taxIdentificationNumber))
        .vatToPay(calculateVatToPay(taxIdentificationNumber))
        .build();
  }

}
