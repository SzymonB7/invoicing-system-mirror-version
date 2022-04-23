package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Invoice;

@RequestMapping("invoices")
@Api(tags = {"invoice-controller"})
public interface InvoiceApi {
  @ApiOperation(value = "Add new invoice to the system")
  @PostMapping(produces = {"application/json;charset=UTF-8"})
  Integer save(@RequestBody Invoice invoice);

  @ApiOperation(value = "List all invoices in the system")
  @GetMapping
  List<Invoice> getAll();

  @ApiOperation(value = "Get invoice by id")
  @GetMapping("/{id}")
  ResponseEntity<Invoice> getById(@PathVariable Integer id);

  @ApiOperation(value = "Update invoice with given id")
  @PutMapping("/{id}")
  ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Invoice updatedInvoice);

  @ApiOperation(value = "Delete invoice with given id")
  @DeleteMapping("/{id}")
  ResponseEntity<?> delete(@PathVariable Integer id);
}
