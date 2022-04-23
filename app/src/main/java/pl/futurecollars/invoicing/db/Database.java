package pl.futurecollars.invoicing.db;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Builder;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

public interface Database {
  Integer save(Invoice invoice);

  Optional<Invoice> getById(Integer id);

  List<Invoice> getAll();

  void update(Integer id, Invoice updatedInvoice);

  void delete(Integer id);

  default BigDecimal visit(Predicate<Invoice> invoicePredicate,
                           Function<InvoiceEntry, BigDecimal> invoiceEntryToAmount) {
    return getAll().stream()
        .flatMap(invoice -> invoice.getInvoiceEntries().stream())
        .map(invoiceEntryToAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}
