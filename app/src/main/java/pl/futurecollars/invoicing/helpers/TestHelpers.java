package pl.futurecollars.invoicing.helpers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

public class TestHelpers {

  public static Company company1 = new Company("Pampers", "11", "Koncertowa 6/8, 80-301 Warszawa");
  public static Company company2 = new Company("Mars", "22", "Slowackiego 6/8, 80-302 Katowice");
  public static Company company3 = new Company("Apple", "33", "Czarnogorska 6/8, 80-303 Gdansk");
  public static InvoiceEntry invoiceEntry1 = new InvoiceEntry("Baton", 1, BigDecimal.valueOf(100), BigDecimal.valueOf(100 * 0.08), Vat.VAT_8);
  public static InvoiceEntry invoiceEntry2 = new InvoiceEntry("Chleb", 1, BigDecimal.valueOf(200), BigDecimal.valueOf(200 * 0.05), Vat.VAT_5);
  public static InvoiceEntry invoiceEntry3 = new InvoiceEntry("Kakao", 1, BigDecimal.valueOf(300), BigDecimal.valueOf(300 * 0.23), Vat.VAT_23);
  public static Invoice invoice1 = new Invoice(LocalDate.of(2020, 10, 11), company1, company2, List.of(invoiceEntry1, invoiceEntry2, invoiceEntry3));
  public static Invoice invoice2 = new Invoice(LocalDate.of(2019, 11, 15), company3, company2, List.of(invoiceEntry1, invoiceEntry2, invoiceEntry3));
  public static Invoice invoice3 = new Invoice(LocalDate.of(2020, 12, 13), company2, company3, List.of(invoiceEntry1, invoiceEntry2));

  public static Company company(int id) {
    return Company.builder()
        .taxIdNumber(String.valueOf(id))
        .address("Polna " + id + ", 80-300 Katowice")
        .name("Majkrosoft " + id)
        .build();
  }

  public static InvoiceEntry product(int id) {
    return InvoiceEntry.builder().description("Windows " + id)
        .quantity(1)
        .price(BigDecimal.valueOf(id * 100))
        .vatValue(BigDecimal.valueOf(100 * 0.08)).vatRate(Vat.VAT_8)
        .build();
  }

  public static Invoice invoice(int id) {
    return Invoice.builder()
        .date(LocalDate.now())
        .sellerCompany(company(id))
        .buyerCompany(company(id + 1))
        .invoiceEntries(List.of(product(id)))
        .build();
  }

}
