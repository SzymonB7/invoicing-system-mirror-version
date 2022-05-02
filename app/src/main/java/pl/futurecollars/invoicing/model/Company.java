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
public class Company {

  @ApiModelProperty(value = "Company name", required = true, example = "Space Ferns Sp.z.o.o.")
  private String name;
  @ApiModelProperty(value = "Tax identification number", required = true, example = "654-321-77-99")
  private String taxIdentificationNumber;
  @ApiModelProperty(value = "Company address", required = true, example = "ul. Polna 13, 80-333 Katowice")
  private String address;
  @Builder.Default
  @ApiModelProperty(value = "Pension insurance amount", required = true, example = "514.57")
  private BigDecimal pensionInsurance = BigDecimal.ZERO;
  @Builder.Default
  @ApiModelProperty(value = "Health insurance amount", required = true, example = "319.94")
  private BigDecimal healthInsurance = BigDecimal.ZERO;

}
