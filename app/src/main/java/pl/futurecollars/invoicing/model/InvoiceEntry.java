package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceEntry {

  @ApiModelProperty(value = "Product/service description", required = true, example = "Bun")
  private String description;
  @ApiModelProperty(value = "Number of items", required = true, example = "5")
  private int quantity;
  @ApiModelProperty(value = "Product/service ner price", required = true, example = "1200.00")
  private BigDecimal price;
  @ApiModelProperty(value = "Product/service tax value", required = true, example = "1200.96")
  private BigDecimal vatValue;
  @ApiModelProperty(value = "Tax rate", required = true)
  private Vat vatRate;

}
