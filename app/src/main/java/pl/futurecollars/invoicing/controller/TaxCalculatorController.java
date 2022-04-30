package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.TaxBill;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@RequestMapping(value = "tax-bill", produces = {"application/json;charset=UTF-8"})
@Api(tags = {"tax-controller"})
@RestController
@AllArgsConstructor
public class TaxCalculatorController {

  private final TaxCalculatorService taxCalculatorService;

  @ApiOperation(value = "Get a summary of income, costs and taxes")
  @PostMapping
  public TaxBill calculateTaxes(@RequestBody Company company) {
    return taxCalculatorService.calculateTaxes(company);
  }
}
