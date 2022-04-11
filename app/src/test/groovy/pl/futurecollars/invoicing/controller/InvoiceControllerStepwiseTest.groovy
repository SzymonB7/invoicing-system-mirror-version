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
import spock.lang.Stepwise

import java.time.LocalDate

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@Stepwise
class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    def invoice = TestHelpers.invoice(7)

    LocalDate updateDate = LocalDate.of(2022, 01, 01)

    def "should return empty array when no invoices where created"() {

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        response == "[]"
    }

    def "should add single invoice"() {
        given:
        def invoiceAsJson = jsonService.writeInvoiceAsJson(invoice)

        when:
        def invoiceId = mockMvc.perform(MockMvcRequestBuilders.post("/invoices")
                .content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        invoiceId == '1'
    }

    def "should return one invoice when getting all invoices"() {

        given:
        def expectedInvoice = invoice
        expectedInvoice.id = 1

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.readJsonAsObject(response, Invoice[])

        then:
        invoices.size() == 1
        invoices[0] == expectedInvoice
    }

    def "should return invoice correctly when getting by id"() {

        given:
        def expectedInvoice = invoice
        expectedInvoice.id = 1

        when:

        def response = mockMvc.perform(MockMvcRequestBuilders.get("/invoices/1"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.readJsonAsObject(response, Invoice)

        then:
        invoices == expectedInvoice

    }

    def "should update invoice"() {

        given:
        def modifiedInvoice = invoice
        modifiedInvoice.date = updateDate

        def invoiceAsJson = jsonService.writeInvoiceAsJson(modifiedInvoice)

        expect:
        mockMvc.perform(MockMvcRequestBuilders.put("/invoices/1")
                .content(invoiceAsJson).
                contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent())
    }

    def "should return invoice with updated date"() {

        given:
        def expectedInvoice = invoice
        expectedInvoice.id = 1
        expectedInvoice.date = updateDate

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.get("/invoices/1"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.readJsonAsObject(response, Invoice)

        then:
        invoices == expectedInvoice

    }

    def "should delete invoice"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/1"))
                .andExpect(status().isNoContent())
        and:
        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/1"))
                .andExpect(status().isNotFound())
        and:
        mockMvc.perform(MockMvcRequestBuilders.get("/invoices/1"))
                .andExpect(status().isNotFound())
    }
}
