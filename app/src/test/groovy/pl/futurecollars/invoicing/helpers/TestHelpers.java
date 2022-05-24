package pl.futurecollars.invoicing.helpers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

public class TestHelpers {

  public static Company company(long id) {
    return Company.builder()
        .id(id)
        .taxIdentificationNumber(String.valueOf(id))
        .address("Polna " + id + ", 80-300 Katowice")
        .name("Majkrosoft " + id)
        .healthInsurance(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(id)).setScale(2))
        .pensionInsurance(BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(id)).setScale(2))
        .build();
  }

  public static InvoiceEntry product(long id) {
    return InvoiceEntry.builder().description("Windows " + id)
        .id(id)
        .quantity(1)
        .netPrice(BigDecimal.valueOf(id * 100L).setScale(2))
        .vatValue(BigDecimal.valueOf(100 * 0.08).setScale(2))
        .vatRate(Vat.VAT_8)
        .carExpenseIsRelatedTo(Car.builder().registrationNumber("GD88822").isUsedForPersonalPurpose(false).build())
        .build();
  }

  public static Invoice invoice(long id) {
    return Invoice.builder()
        .id(id)
        .date(LocalDate.now())
        .number("111/2222/33344/$id")
        .seller(company(id +1))
        .buyer(company(id))
        .invoiceEntries(List.of(product(id)))
        .build();
  }

}
