package pl.futurecollars.invoicing.exceptions;

public class InvoiceNotFoundException extends RuntimeException {

  public InvoiceNotFoundException(String message) {
    super(message);
  }

}
