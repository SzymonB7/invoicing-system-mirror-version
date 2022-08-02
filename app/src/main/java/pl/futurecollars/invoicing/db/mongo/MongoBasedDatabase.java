package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.bson.Document;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class MongoBasedDatabase implements Database {

  private final MongoCollection<Invoice> invoicesMirror;
  private final MongoIdProvider mongoIdProvider;

  @Override
  public Long save(Invoice invoice) {
    invoicesMirror.insertOne(invoice);
    return null;
  }

  @Override
  public Optional<Invoice> getById(long id) {
    return Optional.ofNullable(invoicesMirror.find(idFilter(id)).first());
  }

  @Override
  public List<Invoice> getAll() {

    return StreamSupport
        .stream(invoicesMirror.find().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(Long id, Invoice updatedInvoice) {
    invoicesMirror.findOneAndReplace(idFilter(id), updatedInvoice);

  }

  @Override
  public void delete(Long id) {
    invoicesMirror.findOneAndDelete(idFilter(id));

  }

  private Document idFilter (long id) {
    return new Document("_id", id);
  }
}
