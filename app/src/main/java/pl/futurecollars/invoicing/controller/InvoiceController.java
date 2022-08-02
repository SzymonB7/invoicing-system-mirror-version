package pl.futurecollars.invoicing.controller;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
public class InvoiceController implements InvoiceApi {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @Override
  public Long add(@RequestBody Invoice invoice) {
    return invoiceService.save(invoice);
  }

  @Override
  public List<Invoice> getAll() {
    return invoiceService.getAll();

  }

  @Override
  public ResponseEntity<Invoice> getById(@PathVariable Long id) {
    return invoiceService.getById(id)
        .map(invoice -> ResponseEntity.ok().body(invoice))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Invoice updatedInvoice) {
    try {
      invoiceService.update(id, updatedInvoice);
      return ResponseEntity.noContent().build();
    } catch (InvoiceNotFoundException exception) {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
      invoiceService.delete(id);
      return ResponseEntity.noContent().build();
    } catch (InvoiceNotFoundException exception) {
      return ResponseEntity.notFound().build();
    }
  }

  @Bean
  public InternalResourceViewResolver defaultViewResolver() {
    return new InternalResourceViewResolver();
  }
}
