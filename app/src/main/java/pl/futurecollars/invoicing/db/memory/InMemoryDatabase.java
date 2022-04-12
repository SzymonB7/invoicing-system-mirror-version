package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException;
import pl.futurecollars.invoicing.model.Invoice;

public class InMemoryDatabase implements Database {
  private final Map<Integer, Invoice> invoices = new HashMap<>();
  private Integer nextId = 1;

  @Override
  public Integer save(Invoice invoice) {
    invoice.setId(nextId);
    invoices.put(nextId, invoice);
    return nextId++;
  }

  @Override
  public Optional<Invoice> getById(Integer id) {
    return Optional.ofNullable(invoices.get(id));
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices.values());
  }

  @Override
  public void update(Integer id, Invoice updatedInvoice) {
    if (!invoices.containsKey(id)) {
      throw new InvoiceNotFoundException("Id " + id + " does not exist");
    }
    updatedInvoice.setId(id);
    invoices.put(id, updatedInvoice);
  }

  @Override
  public void delete(Integer id) {
    if (!invoices.containsKey(id)) {
      throw new InvoiceNotFoundException("Id " + id + " does not exist");
    }
    invoices.remove(id);
  }
}
