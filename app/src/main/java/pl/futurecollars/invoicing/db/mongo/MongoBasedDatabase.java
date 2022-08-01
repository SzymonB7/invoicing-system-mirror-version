package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClientFactory;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class MongoBasedDatabase implements Database {

  private final MongoCollection<Invoice> collection;
  private final MongoIdProvider mongoIdProvider;

  @Override
  public Long save(Invoice invoice) {
    collection.insertOne(invoice);
    return null;
  }

  @Override
  public Optional<Invoice> getById(long id) {
    return Optional.empty();
  }

  @Override
  public List<Invoice> getAll() {
    return null;
  }

  @Override
  public void update(Long id, Invoice updatedInvoice) {

  }

  @Override
  public void delete(Long id) {

  }
}
