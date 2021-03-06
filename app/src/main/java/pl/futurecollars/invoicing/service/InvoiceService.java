package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@Service
public class InvoiceService {

  private final Database database;

  public InvoiceService(Database database) {
    this.database = database;
  }

  public Integer save(Invoice invoice) {
    return database.save(invoice);
  }

  public Optional<Invoice> getById(Integer id) {
    return database.getById(id);
  }

  public List<Invoice> getAll() {
    return database.getAll();
  }

  public void update(Integer id, Invoice updatedInvoice) {
    database.update(id, updatedInvoice);
  }

  public void delete(Integer id) {
    database.delete(id);
  }

}
