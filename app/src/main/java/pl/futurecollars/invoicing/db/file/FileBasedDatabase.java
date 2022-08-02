package pl.futurecollars.invoicing.db.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class FileBasedDatabase implements Database {
  private final FileService fileService;
  private final JsonService jsonService;
  private final IdService idService;
  private final Path databasePath;

  @Override
  public Long save(Invoice invoice) {
    try {
      invoice.setId(idService.getNextIdAndIncrement());
      String invoiceAsJson = jsonService.writeObjectAsJson(invoice);
      fileService.appendLineToFile(databasePath, invoiceAsJson);
      return invoice.getId();
    } catch (IOException e) {
      throw new RuntimeException("Failed to save invoice in database", e);
    }
  }

  @Override
  public Optional<Invoice> getById(long id) {

    try {

      BufferedReader bufferedReader = Files.newBufferedReader(databasePath);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.contains("\"id\"" + ":" + id + ",\"number\"")) {
          return Optional.of(jsonService.readJsonAsObject(line, Invoice.class));
        }

      }
      return Optional.empty();
    } catch (IOException e) {
      throw new RuntimeException("Failed to get an invoice from file", e);
    }

  }

  @Override
  public List<Invoice> getAll() {

    try {
      return fileService.readAllLines(databasePath)
          .stream()
          .map(line -> jsonService.readJsonAsObject(line, Invoice.class))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Failed to get a list of invoices from file");
    }
  }

  @Override
  public void update(Long id, Invoice updatedInvoice) {
    try {
      List<String> invoicesInDatabase = fileService.readAllLines(databasePath);
      updatedInvoice.setId(id);
      String updatedInvoiceAsJson = jsonService.writeObjectAsJson(updatedInvoice);
      int invoicesUpdated = 0;
      for (String invoice : invoicesInDatabase) {
        if (invoice.contains("\"id\"" + ":" + id + ",\"number\"")) {
          invoicesInDatabase.set(invoicesInDatabase.indexOf(invoice), updatedInvoiceAsJson);
          invoicesUpdated++;
        }
      }
      if (invoicesUpdated == 0) {
        throw new InvoiceNotFoundException("Id" + id + "does not exist");
      }
      invoicesInDatabase.set(id.intValue() - 1, updatedInvoiceAsJson);
      fileService.overwriteLinesInFile(databasePath, invoicesInDatabase);
    } catch (IOException e) {
      throw new RuntimeException("Failed to update invoice id:" + id + "in database");
    }
  }

  @Override
  public void delete(Long id) {
    try {
      List<String> invoicesInDatabase = fileService.readAllLines(databasePath);
      int invoicesRemoved = 0;
      for (Iterator<String> iterator = invoicesInDatabase.iterator(); iterator.hasNext(); ) {
        String invoice = iterator.next();
        if (invoice.contains("\"id\"" + ":" + id + ",\"number\"")) {
          iterator.remove();
          invoicesRemoved++;
        }
      }
      if (invoicesRemoved == 0) {
        throw new InvoiceNotFoundException("Id" + id + "does not exist");
      }
      fileService.overwriteLinesInFile(databasePath, invoicesInDatabase);

    } catch (IOException e) {
      throw new RuntimeException("Failed to delete invoice id:" + id + "from database");
    }
  }
}
