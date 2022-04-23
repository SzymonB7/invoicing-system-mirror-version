package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@AllArgsConstructor
public class TaxCalculatorService {
  private final Database database;

  public Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
    return invoice -> invoice.getBuyerCompany().getTaxIdNumber().equals(taxIdentificationNumber);
  }

  public Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
    return invoice -> invoice.getSellerCompany().getTaxIdNumber().equals(taxIdentificationNumber);
  }

  public BigDecimal calculateIncomingVat(String taxIdentificationNumber) {
    return database.visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal calculateOutgoingVat(String taxIdentificationNumber) {
    return database.visit(buyerPredicate(taxIdentificationNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal calculateIncome(String taxIdentificationNumber) {
    return database.visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getPrice);
  }

  public BigDecimal calculateCosts(String taxIdentificationNumber) {
    return database.visit(buyerPredicate(taxIdentificationNumber), InvoiceEntry::getPrice);
  }

  public BigDecimal calculateEarnings(String taxIdentificationNumber) {
    return calculateIncome(taxIdentificationNumber).subtract(calculateCosts(taxIdentificationNumber));
  }

  public BigDecimal calculateVatToPay(String taxIdentificationNumber) {
    return calculateIncomingVat(taxIdentificationNumber).subtract(calculateOutgoingVat(taxIdentificationNumber));
  }

  public TaxBill calculateTaxes(String taxIdentificationNumber) {
    return TaxBill.builder()
        .income(calculateIncome(taxIdentificationNumber))
        .costs(calculateCosts(taxIdentificationNumber))
        .incomingVat(calculateIncomingVat(taxIdentificationNumber))
        .outgoingVat(calculateOutgoingVat(taxIdentificationNumber))
        .earnings(calculateEarnings(taxIdentificationNumber))
        .vatToPay(calculateVatToPay(taxIdentificationNumber))
        .build();
  }

}
