package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Car {
  @ApiModelProperty(value = "Car id (generated by application)", required = true, example = "1")
  private int id;
  @ApiModelProperty(value = "Car registration number", required = true, example = "GD 35577")
  private String registrationNumber;
  @ApiModelProperty(value = "Specifies if car is also used for personal reasons", required = true, example = "true")
  private boolean isUsedForPersonalPurpose;
}