package pl.futurecollars.invoicing.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.exceptions.InvoiceNotFoundException;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@RequestMapping("invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @PostMapping(produces = {"application/json;charset=UTF-8"})
  public Integer save(@RequestBody Invoice invoice) {
    return invoiceService.save(invoice);
  }

  @GetMapping
  public List<Invoice> getAll() {
    return invoiceService.getAll();

  }

  @GetMapping("/{id}")
  public ResponseEntity<Invoice> getById(@PathVariable Integer id) {
    return invoiceService.getById(id)
        .map(invoice -> ResponseEntity.ok().body(invoice))
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Invoice updatedInvoice) {
    try {
      invoiceService.update(id, updatedInvoice);
      return ResponseEntity.noContent().build();
    } catch (InvoiceNotFoundException exception) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Integer id) {
    try {
      invoiceService.delete(id);
      return ResponseEntity.noContent().build();
    } catch (InvoiceNotFoundException exception) {
      return ResponseEntity.notFound().build();
    }
  }
}
