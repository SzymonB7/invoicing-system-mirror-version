package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
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
  private String taxIdNumber;
  @ApiModelProperty(value = "Company address", required = true, example = "ul. Polna 13, 80-333 Katowice")
  private String address;

}
