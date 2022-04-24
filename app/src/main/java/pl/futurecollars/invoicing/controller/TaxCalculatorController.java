package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.TaxBill;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@RequestMapping("tax-bill")
@Api(tags = {"tax-controller"})
@RestController
@AllArgsConstructor
public class TaxCalculatorController {

  private final TaxCalculatorService taxCalculatorService;

  @ApiOperation(value = "Get a summary of income, costs and taxes")
  @GetMapping(value = "/{taxIdentificationNumber}")
  public TaxBill getTaxBill(@PathVariable String taxIdentificationNumber) {
    return taxCalculatorService.calculateTaxes(taxIdentificationNumber);
  }
}
