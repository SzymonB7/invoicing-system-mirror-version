package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@AllArgsConstructor
public class SqlDatabase implements Database {

  private JdbcTemplate jdbcTemplate;
  private final Map<Vat, Integer> vatToId = new HashMap<>();
  private final Map<Integer, Vat> idToVat = new HashMap<>();

  @PostConstruct
  void initVatRatesMap() {
    jdbcTemplate.query("select * from vat",
        rs -> {
          Vat vat = Vat.valueOf("VAT_" + rs.getString("name"));
          int id = rs.getInt("id");
          vatToId.put(vat, id);
          idToVat.put(id, vat);
        });
  }

  private Integer insertCarAndGetItsId(Car car) {
    if (car == null) {
      return null;
    }

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
      "insert into car (registration_number, is_used_for_personal_purpose) values (?, ?);", new String[] {"id"});
      ps.setString(1, car.getRegistrationNumber());
      ps.setBoolean(2, car.isUsedForPersonalPurpose());
      return ps;
    }, keyHolder);

    return keyHolder.getKey().intValue();

  }

  @Override
  public Integer save(Invoice invoice) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
      "insert into company (name, address) values (?, ?);", new String[] {"id"});
      ps.setString( 1, invoice.getBuyer().getName());
      ps.setString( 2, invoice.getBuyer().getAddress());
      return ps;
    }, keyHolder);

    long buyerId = keyHolder.getKey().longValue();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "insert into company (name, address) values (?, ?);", new String[] {"id"});
      ps.setString( 1, invoice.getSeller().getName());
      ps.setString( 2, invoice.getSeller().getAddress());
      return ps;
    }, keyHolder);

    long sellerId = keyHolder.getKey().longValue();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "insert into invoice (date, number, buyer, seller) values (?, ?, ?, ?);", new String[] {"id"});
        ps.setDate(1, Date.valueOf(invoice.getDate()));
        ps.setString(2, invoice.getNumber());
        ps.setLong(3, buyerId);
        ps.setLong(4, sellerId);
        return ps;
        }, keyHolder);

    int invoiceId = keyHolder.getKey().intValue();

    invoice.getInvoiceEntries().forEach(invoiceEntry -> {jdbcTemplate.update(connection -> {
    PreparedStatement ps = connection.prepareStatement(
        "insert into invoice_entry (description, quantity, net_price, vat_value, vat_rate, car_expense_is_related_to) values (?, ?, ?, ?, ?, ?);",
        new String[] {"id"});
      ps.setString(1, invoiceEntry.getDescription());
      ps.setInt(2, invoiceEntry.getQuantity());
      ps.setBigDecimal(3, invoiceEntry.getNetPrice());
      ps.setBigDecimal(4, invoiceEntry.getVatValue());
      ps.setInt(5, vatToId.get(invoiceEntry.getVatRate()));
      ps.setObject(6, insertCarAndGetItsId(invoiceEntry.getCarExpenseIsRelatedTo()));
      return ps;
    }, keyHolder);

      int invoiceEntryId = keyHolder.getKey().intValue();

      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(
            "insert into invoice_invoice_entry (invoice_id, invoice_entry_id) values (?, ?);");
        ps.setInt(1, invoiceId);
        ps.setInt(2, invoiceEntryId);
        return ps;
        });
      });
    return invoiceId;
}



  @Override
  public Optional<Invoice> getById(Integer id) {
    return Optional.empty();
  }

  @Override
  public List<Invoice> getAll() {
    return jdbcTemplate.query("select i.id, i.date, i.number, c1.name as seller_name, c2.name as buyer_name from invoice i"
            + " inner join company c1 on i.seller = c1.id"
            + " inner join company c2 on i.buyer = c2.id",
        (rs, rowNr) -> {
      int invoiceId = rs.getInt("id");

      List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
          "select * from invoice_invoice_entry iie"
              + " inner join invoice_entry e on iie.invoice_entry_id = e.id"
              + " left outer join car c on e.car_expense_is_related_to = c.id"
              + " where invoice_id = " + invoiceId, (response, ignored) -> InvoiceEntry.builder()
              .id(response.getInt("id"))
              .description(response.getString("description"))
              .quantity(response.getInt( "quantity"))
              .netPrice(response.getBigDecimal( "net_price"))
              .vatValue(response.getBigDecimal( "vat_value"))
              .vatRate(idToVat.get(response.getInt( "vat_rate")))
              .build());

        return Invoice.builder()
            .id(rs.getInt("id"))
            .date(rs.getDate("date").toLocalDate())
            .number(rs.getString("number"))
            .buyer(Company.builder().name(rs.getString("buyer_name")).build())
            .seller(Company.builder().name(rs.getString("seller_name")).build())
            .invoiceEntries(invoiceEntries)
            .build();

    });
  }

  @Override
  public void update(Integer id, Invoice updatedInvoice) {

  }

  @Override
  public void delete(Integer id) {

  }
}