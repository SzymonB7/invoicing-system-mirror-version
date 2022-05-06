package pl.futurecollars.invoicing.db.sql;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class SqlDatabase implements Database {

  private JdbcTemplate jdbcTemplate;

  @Override
  public Integer save(Invoice invoice) {
    return null;
  }

  @Override
  public Optional<Invoice> getById(Integer id) {
    return Optional.empty();
  }

  @Override
  public List<Invoice> getAll() {
    return null;
  }

  @Override
  public void update(Integer id, Invoice updatedInvoice) {

  }

  @Override
  public void delete(Integer id) {

  }
}
