package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

class JsonServiceTest extends Specification {
    JsonService jsonService

    void setup() {
        jsonService = new JsonService()
    }

    def "should write invoice to file and read it back to invoice"() {
        given:
        Invoice invoice = TestHelpers.invoice(1)
        when:
        String invoiceAsJson = jsonService.writeObjectAsJson(invoice)
        and:
        Invoice invoiceReadBackFromJson = jsonService.readJsonAsObject(invoiceAsJson, Invoice)
        then:
        invoice == invoiceReadBackFromJson
    }

}
