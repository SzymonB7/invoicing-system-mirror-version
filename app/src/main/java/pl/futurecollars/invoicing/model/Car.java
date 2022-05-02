package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Car {
  @ApiModelProperty(value = "Car registration number", required = true, example = "GD 35577")
  private String registrationNumber;
  @ApiModelProperty(value = "Specifies if car is also used for personal reasons", required = true, example = "true")
  private boolean isUsedForPersonalPurpose;
}
