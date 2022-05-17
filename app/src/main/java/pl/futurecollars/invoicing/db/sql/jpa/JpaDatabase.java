package pl.futurecollars.invoicing.db.sql.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class JpaDatabase implements Database {

  private final InvoiceRepository invoiceRepository;

  @Override
  public Long save(Invoice invoice) {
    return invoiceRepository.save(invoice).getId();
  }

  @Override
  public Optional<Invoice> getById(long id) {
    return invoiceRepository.findById(id);
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport
        .stream(invoiceRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(Long id, Invoice updatedInvoice) {

    Optional<Invoice> invoiceOptional = getById(id);
    if (invoiceOptional.isEmpty()) {
      throw new InvoiceNotFoundException("Id" + id + "does not exist");
    }
    updatedInvoice.setId(id);
    updatedInvoice.getBuyer().setId(id);
    updatedInvoice.getSeller().setId(id);

    invoiceRepository.save(updatedInvoice);

  }

  @Override
  public void delete(Long id) {

    Optional<Invoice> invoice = getById(id);

    if (invoice.isEmpty()) {
      throw new InvoiceNotFoundException("Id" + id + "does not exist");
    }
    invoice.ifPresent(invoiceRepository::delete);
  }
}
