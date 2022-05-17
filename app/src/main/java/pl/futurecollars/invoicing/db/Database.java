package pl.futurecollars.invoicing.db;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

public interface Database {
  Long save(Invoice invoice);

  Optional<Invoice> getById(long id);

  List<Invoice> getAll();

  void update(Long id, Invoice updatedInvoice);

  void delete(Long id);

  default BigDecimal visit(Predicate<Invoice> invoicePredicate,
                           Function<InvoiceEntry, BigDecimal> invoiceEntryToAmount) {
    return getAll().stream()
        .filter(invoicePredicate)
        .flatMap(invoice -> invoice.getInvoiceEntries().stream())
        .map(invoiceEntryToAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  default void reset() {
    getAll().forEach(invoice -> delete(invoice.getId()));
  }
}
