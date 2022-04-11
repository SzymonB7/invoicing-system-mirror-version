package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.futurecollars.invoicing.db.file.JsonService
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class InvoiceControllerIntegrationTest extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

//    def invoice1 = TestHelpers.invoice(1)
//    def invoice2 = TestHelpers.invoice(7)
//    def invoice3 = TestHelpers.invoice(5)

    def 'should add multiple invoices with post method returning correct ids'() {
        given:
        def invoice1 = TestHelpers.invoice(1)
        def invoice2 = TestHelpers.invoice(5)
        def invoice3 = TestHelpers.invoice(4)

        def invoiceAsJson1 = jsonService.writeObjectAsJson(invoice1)
        def invoiceAsJson2 = jsonService.writeObjectAsJson(invoice2)
        def invoiceAsJson3 = jsonService.writeObjectAsJson(invoice3)

        when:

        def invoice1Id = mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                .content(invoiceAsJson1).contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice2Id = mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                .content(invoiceAsJson2).contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice3Id = mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                .content(invoiceAsJson3).contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        invoice1Id == "1"
        invoice2Id == "2"
        invoice3Id == "3"

    }
}
