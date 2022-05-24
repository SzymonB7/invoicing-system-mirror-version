package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@AllArgsConstructor
public class SqlDatabase implements Database {

  private JdbcTemplate jdbcTemplate;
//  private final Map<Vat, Integer> vatToId = new HashMap<>();
//  private final Map<Integer, Vat> idToVat = new HashMap<>();
//
//  @PostConstruct
//  void initVatRatesMap() {
//    jdbcTemplate.query("select * from vat",
//        rs -> {
//          Vat vat = Vat.valueOf("VAT_" + rs.getString("name"));
//          int id = rs.getInt("id");
//          vatToId.put(vat, id);
//          idToVat.put(id, vat);
//        });
//  }

  private Long insertCarAndGetItsId(Car car) {
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

    return keyHolder.getKey().longValue();

  }

  @Override
  @Transactional
  public Long save(Invoice invoice) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "insert into company (name, address, tax_identification_number, pension_insurance, health_insurance) values (?, ?, ?, ?, ?);",
          new String[] {"id"});
      ps.setString(1, invoice.getBuyer().getName());
      ps.setString(2, invoice.getBuyer().getAddress());
      ps.setString(3, invoice.getBuyer().getTaxIdentificationNumber());
      ps.setBigDecimal(4, invoice.getBuyer().getPensionInsurance());
      ps.setBigDecimal(5, invoice.getBuyer().getHealthInsurance());
      return ps;
    }, keyHolder);

    long buyerId = keyHolder.getKey().longValue();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "insert into company (name, address, tax_identification_number, pension_insurance, health_insurance) values (?, ?, ?, ?, ?);",
          new String[] {"id"});
      ps.setString(1, invoice.getSeller().getName());
      ps.setString(2, invoice.getSeller().getAddress());
      ps.setString(3, invoice.getSeller().getTaxIdentificationNumber());
      ps.setBigDecimal(4, invoice.getSeller().getPensionInsurance());
      ps.setBigDecimal(5, invoice.getSeller().getHealthInsurance());
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

    Long invoiceId = keyHolder.getKey().longValue();

    invoice.getInvoiceEntries().forEach(invoiceEntry -> {
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(
            "insert into invoice_entry (description, quantity, net_price, vat_value, vat_rate, car_expense_is_related_to) values (?, ?, ?, ?, ?, ?);",
            new String[] {"id"});
        ps.setString(1, invoiceEntry.getDescription());
        ps.setInt(2, invoiceEntry.getQuantity());
        ps.setBigDecimal(3, invoiceEntry.getNetPrice());
        ps.setBigDecimal(4, invoiceEntry.getVatValue());
        ps.setString(5, invoiceEntry.getVatRate().name());
        ps.setObject(6, insertCarAndGetItsId(invoiceEntry.getCarExpenseIsRelatedTo()));
        return ps;
      }, keyHolder);

      Long invoiceEntryId = keyHolder.getKey().longValue();

      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(
            "insert into invoice_invoice_entry (invoice_id, invoice_entry_id) values (?, ?);");
        ps.setLong(1, invoiceId);
        ps.setLong(2, invoiceEntryId);
        return ps;
      });
    });
    return invoiceId;
  }

  @Override
  public Optional<Invoice> getById(long id) {
    List<Invoice> invoices = jdbcTemplate.query("select i.id, i.date, i.number, "
        + "c1.id as seller_id, c1.name as seller_name, c1.tax_identification_number as seller_tax_id, c1.address as seller_address, "
        + "c1.pension_insurance as seller_pension_insurance, c1.health_insurance as seller_health_insurance, "
        + "c2.id as buyer_id, c2.name as buyer_name, c2.tax_identification_number as buyer_tax_id, c2.address as buyer_address, "
        + "c2.pension_insurance as buyer_pension_insurance, c2.health_insurance as buyer_health_insurance "
        + "from invoice i "
        + "inner join company c1 on i.seller = c1.id "
        + "inner join company c2 on i.buyer = c2.id " + "where i.id = " + id, (rs, rowNr) -> {
      int invoiceId = rs.getInt("id");

      List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
          "select * from invoice_invoice_entry iie"
              + " inner join invoice_entry e on iie.invoice_entry_id = e.id"
              + " left outer join car c on e.car_expense_is_related_to = c.id"
              + " where invoice_id = " + invoiceId, (response, ignored) -> InvoiceEntry.builder()
              .id(response.getInt("id"))
              .description(response.getString("description"))
              .quantity(response.getInt("quantity"))
              .netPrice(response.getBigDecimal("net_price"))
              .vatValue(response.getBigDecimal("vat_value"))
              .vatRate(Vat.valueOf(response.getString("vat_rate")))
              .carExpenseIsRelatedTo(response.getObject("registration_number") != null
                  ? Car.builder()
                  .registrationNumber(response.getString("registration_number"))
                  .isUsedForPersonalPurpose(response.getBoolean("is_used_for_personal_purpose"))
                  .build()
                  : null)
              .build());

      return Invoice.builder()
          .id(rs.getLong("id"))
          .date(rs.getDate("date").toLocalDate())
          .number(rs.getString("number"))
          .buyer(Company.builder()
              .id(rs.getLong("buyer_id"))
              .taxIdentificationNumber(rs.getString("buyer_tax_id"))
              .name(rs.getString("buyer_name"))
              .address(rs.getString("buyer_address"))
              .pensionInsurance(rs.getBigDecimal("buyer_pension_insurance"))
              .healthInsurance(rs.getBigDecimal("buyer_health_insurance"))
              .build()
          )
          .seller(Company.builder()
              .id(rs.getLong("seller_id"))
              .taxIdentificationNumber(rs.getString("seller_tax_id"))
              .name(rs.getString("seller_name"))
              .address(rs.getString("seller_address"))
              .pensionInsurance(rs.getBigDecimal("seller_pension_insurance"))
              .healthInsurance(rs.getBigDecimal("seller_health_insurance"))
              .build()
          )
          .invoiceEntries(invoiceEntries)
          .build();

    });

    return invoices.isEmpty() ? Optional.empty() : Optional.of(invoices.get(0));
  }

  @Override
  public List<Invoice> getAll() {
    return jdbcTemplate.query("select i.id, i.date, i.number, "
            + "c1.id as seller_id, c1.name as seller_name, c1.tax_identification_number as seller_tax_id, c1.address as seller_address, "
            + "c1.pension_insurance as seller_pension_insurance, c1.health_insurance as seller_health_insurance, "
            + "c2.id as buyer_id, c2.name as buyer_name, c2.tax_identification_number as buyer_tax_id, c2.address as buyer_address, "
            + "c2.pension_insurance as buyer_pension_insurance, c2.health_insurance as buyer_health_insurance "
            + "from invoice i "
            + "inner join company c1 on i.seller = c1.id "
            + "inner join company c2 on i.buyer = c2.id",
        (rs, rowNr) -> {
          int invoiceId = rs.getInt("id");

          List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
              "select * from invoice_invoice_entry iie"
                  + " inner join invoice_entry e on iie.invoice_entry_id = e.id"
                  + " left outer join car c on e.car_expense_is_related_to = c.id"
                  + " where invoice_id = " + invoiceId, (response, ignored) -> InvoiceEntry.builder()
                  .id(response.getInt("id"))
                  .description(response.getString("description"))
                  .quantity(response.getInt("quantity"))
                  .netPrice(response.getBigDecimal("net_price"))
                  .vatValue(response.getBigDecimal("vat_value"))
                  .vatRate(Vat.valueOf(response.getString("vat_rate")))
                  .carExpenseIsRelatedTo(response.getObject("registration_number") != null
                      ? Car.builder()
                      .registrationNumber(response.getString("registration_number"))
                      .isUsedForPersonalPurpose(response.getBoolean("is_used_for_personal_purpose"))
                      .build()
                      : null)
                  .build());

          return Invoice.builder()
              .id(rs.getLong("id"))
              .date(rs.getDate("date").toLocalDate())
              .number(rs.getString("number"))
              .buyer(Company.builder()
                  .id(rs.getLong("buyer_id"))
                  .taxIdentificationNumber(rs.getString("buyer_tax_id"))
                  .name(rs.getString("buyer_name"))
                  .address(rs.getString("buyer_address"))
                  .pensionInsurance(rs.getBigDecimal("buyer_pension_insurance"))
                  .healthInsurance(rs.getBigDecimal("buyer_health_insurance"))
                  .build()
              )
              .seller(Company.builder()
                  .id(rs.getLong("seller_id"))
                  .taxIdentificationNumber(rs.getString("seller_tax_id"))
                  .name(rs.getString("seller_name"))
                  .address(rs.getString("seller_address"))
                  .pensionInsurance(rs.getBigDecimal("seller_pension_insurance"))
                  .healthInsurance(rs.getBigDecimal("seller_health_insurance"))
                  .build()
              )
              .invoiceEntries(invoiceEntries)
              .build();

        });
  }

  @Override
  @Transactional
  public void update(Long id, Invoice updatedInvoice) {
    Optional<Invoice> originalInvoice = getById(id);

    if (originalInvoice.isEmpty()) {
      throw new InvoiceNotFoundException("Id" + id + "does not exist");
    }

    updateCompany(updatedInvoice.getBuyer(), originalInvoice.get().getBuyer());
    updateCompany(updatedInvoice.getSeller(), originalInvoice.get().getSeller());

    jdbcTemplate.update(connection -> {
      PreparedStatement ps =
          connection.prepareStatement(
              "update invoice "
                  + "set date=?, "
                  + "number=? "
                  + "where id=?"
          );
      ps.setDate(1, Date.valueOf(updatedInvoice.getDate()));
      ps.setString(2, updatedInvoice.getNumber());
      ps.setLong(3, id);
      return ps;
    });

    jdbcTemplate.update(connection -> { // warn: makes use of delete cascade
      PreparedStatement ps = connection.prepareStatement("delete from car where id in ("
          + "select car_expense_is_related_to from invoice_entry where id in ("
          + "select invoice_entry_id from invoice_invoice_entry where invoice_id=?));");
      ps.setLong(1, id);
      return ps;
    });

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from invoice_entry where id in (select invoice_entry_id from invoice_invoice_entry where invoice_id=?);");
      ps.setLong(1, id);
      return ps;
    });

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    updatedInvoice.getInvoiceEntries().forEach(entry -> {
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection
            .prepareStatement(
                "insert into invoice_entry (description, quantity, net_price, vat_value, vat_rate, car_expense_is_related_to) "
                    + "values (?, ?, ?, ?, ?, ?);",
                new String[] {"id"});
        ps.setString(1, entry.getDescription());
        ps.setInt(2, entry.getQuantity());
        ps.setBigDecimal(3, entry.getNetPrice());
        ps.setBigDecimal(4, entry.getVatValue());
        ps.setString(5, entry.getVatRate().name());
        ps.setObject(6, insertCarAndGetItsId(entry.getCarExpenseIsRelatedTo()));
        return ps;
      }, keyHolder);

      Long invoiceEntryId = keyHolder.getKey().longValue();

      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(
            "insert into invoice_invoice_entry (invoice_id, invoice_entry_id) values (?, ?);");
        ps.setLong(1, id);
        ps.setLong(2, invoiceEntryId);
        return ps;
      });
    });

  }

  private void updateCompany(Company buyer, Company buyer2) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "update company "
              + "set name=?, "
              + "address=?, "
              + "tax_identification_number=?, "
              + "health_insurance=?, "
              + "pension_insurance=? "
              + "where id=?"
      );
      ps.setString(1, buyer.getName());
      ps.setString(2, buyer.getAddress());
      ps.setString(3, buyer.getTaxIdentificationNumber());
      ps.setBigDecimal(4, buyer.getHealthInsurance());
      ps.setBigDecimal(5, buyer.getPensionInsurance());
      ps.setLong(6, buyer2.getId());
      return ps;
    });
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Optional<Invoice> invoiceOptional = getById(id);
    if (invoiceOptional.isEmpty()) {
      throw new InvoiceNotFoundException("Id" + id + "does not exist");
    }

    Invoice invoice = invoiceOptional.get();

    jdbcTemplate.update(connection -> { // warn: makes use of delete cascade
      PreparedStatement ps = connection.prepareStatement("delete from car where id in ("
          + "select car_expense_is_related_to from invoice_entry where id in ("
          + "select invoice_entry_id from invoice_invoice_entry where invoice_id=?));");
      ps.setLong(1, id);
      return ps;
    });

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from invoice_entry where id in (select invoice_entry_id from invoice_invoice_entry where invoice_id=?);");
      ps.setLong(1, id);
      return ps;
    });

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from invoice where id = ?;");
      ps.setLong(1, id);
      return ps;
    });

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from company where id in (?, ?);");
      ps.setLong(1, invoice.getBuyer().getId());
      ps.setLong(2, invoice.getSeller().getId());
      return ps;
    });
  }
}
