package pl.futurecollars.invoicing.helpers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

public class TestHelpers {

  public static Company company(int id) {
    return Company.builder()
        .taxIdentificationNumber(String.valueOf(id))
        .address("Polna " + id + ", 80-300 Katowice")
        .name("Majkrosoft " + id)
        .healthInsurance(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(id)))
        .pensionInsurance(BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(id)))
        .build();
  }

  public static InvoiceEntry product(int id) {
    return InvoiceEntry.builder().description("Windows " + id)
        .quantity(1)
        .netPrice(BigDecimal.valueOf(id * 100L))
        .vatValue(BigDecimal.valueOf(100 * 0.08))
        .vatRate(Vat.VAT_8)
        .build();
  }

  public static Invoice invoice(int id) {
    return Invoice.builder()
        .date(LocalDate.now())
        .seller(company(id))
        .buyer(company(id + 1))
        .invoiceEntries(List.of(product(id)))
        .build();
  }

}
