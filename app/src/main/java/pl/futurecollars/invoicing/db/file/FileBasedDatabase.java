package pl.futurecollars.invoicing.db.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
  public Integer save(Invoice invoice) {
    try {
      invoice.setId(idService.getNextIdAndIncrement());
      String invoiceAsJson = jsonService.writeInvoiceAsJson(invoice);
      fileService.appendLineToFile(databasePath, invoiceAsJson);
      return invoice.getId();
    } catch (IOException e) {
      throw new RuntimeException("Failed to save invoice in database", e);
    }
  }

  @Override
  public Optional<Invoice> getById(Integer id) {

    try {

      BufferedReader bufferedReader = Files.newBufferedReader(databasePath);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.contains("\"id\"" + ":" + id)) {
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
  public void update(Integer id, Invoice updatedInvoice) {
    try {
      List<String> invoicesInDatabase = fileService.readAllLines(databasePath);
      updatedInvoice.setId(id);
      String updatedInvoiceAsJson = jsonService.writeInvoiceAsJson(updatedInvoice);
      invoicesInDatabase.set(id - 1, updatedInvoiceAsJson);
      fileService.overwriteLinesInFile(databasePath, invoicesInDatabase);
    } catch (IOException e) {
      throw new RuntimeException("Failed to update invoice id:" + id + "in database");
    } catch (IndexOutOfBoundsException e) {
      throw new InvoiceNotFoundException("Id" + id + "does not exist");
    }
  }

  @Override
  public void delete(Integer id) {
    try {
      List<String> invoicesInDatabase = fileService.readAllLines(databasePath);
      invoicesInDatabase.remove(id - 1);
      fileService.overwriteLinesInFile(databasePath, invoicesInDatabase);

    } catch (IOException e) {
      throw new RuntimeException("Failed to delete invoice id:" + id + "from database");
    } catch (IndexOutOfBoundsException e) {
      throw new InvoiceNotFoundException("Id" + id + "does not exist");
    }
  }
}
