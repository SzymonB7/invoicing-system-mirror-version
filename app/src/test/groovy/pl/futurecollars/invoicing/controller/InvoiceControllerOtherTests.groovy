package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.db.file.JsonService
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import java.time.LocalDate
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InvoiceControllerOtherTests extends Specification {

    @Autowired
    private MockMvc mockMvc
    @Autowired
    private JsonService jsonService

    def "helper_post"(
            Invoice invoice) {

        def invoiceAsJson = jsonService.writeObjectAsJson(invoice)

        def response = mockMvc.perform(post("/invoices").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return response
    }


    def 'should get correct invoice by id 2'() {
        given:
        def invoice1 = TestHelpers.invoice(1)
        invoice1.setId(1)
        def invoice2 = TestHelpers.invoice(2)
        invoice2.setId(2)
        def invoice3 = TestHelpers.invoice(3)
        invoice3.setId(3)

        when:
        helper_post(invoice1)
        helper_post(invoice2)
        helper_post(invoice3)

        String invoiceId2GetResponse = mockMvc.perform(get("/invoices/2"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .getContentAsString()

        Invoice invoiceId2Response = jsonService.readJsonAsObject(invoiceId2GetResponse, Invoice)

        then:
        invoiceId2Response == invoice2

    }

    def "should return empty array and status ok when get all invoices is called"() {
        when:
        def response = mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        response == "[]"

    }

    def "should return status not found when invoice with wrong id was called"() {
        given:
        def invoice1 = TestHelpers.invoice(1)
        invoice1.setId(1)
        def invoice2 = TestHelpers.invoice(2)
        invoice2.setId(2)
        def invoice3 = TestHelpers.invoice(3)
        invoice3.setId(3)

        helper_post(invoice1)
        helper_post(invoice2)
        helper_post(invoice3)

        expect:
        mockMvc.perform(get("/invoices/1"))
                .andExpect(status().isOk())

        and:
        mockMvc.perform(get("/invoices/2"))
                .andExpect(status().isOk())

        and:
        mockMvc.perform(get("/invoices/3"))
                .andExpect(status().isOk())

        and:
        mockMvc.perform(get("/invoices/4"))
                .andExpect(status().isNotFound())

    }

    def "should post 1 invoice"() {

        given:
        def invoice1 = TestHelpers.invoice(1)
        invoice1.setId(1)
        def invoiceAsJson = jsonService.writeObjectAsJson(invoice1)

        when:
        def response = mockMvc.perform(post("/invoices").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        response == "1"

    }


    def "should post 3 invoices"() {
        given:
        def invoice1 = TestHelpers.invoice(1)
        invoice1.setId(1)
        def invoice2 = TestHelpers.invoice(2)
        invoice2.setId(2)
        def invoice3 = TestHelpers.invoice(3)
        invoice3.setId(3)

        when:
        def response1 = helper_post(invoice1)
        def response2 = helper_post(invoice2)
        def response3 = helper_post(invoice3)

        String responseGetAll = mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .getContentAsString()


        def invoiceList = jsonService.readJsonAsObject(responseGetAll, Invoice[])

        then:
        response1 == "1"
        response2 == "2"
        response3 == "3"
        invoiceList.size() == 3

    }

    def "should post 2 invoices and delete 1 invoice"() {

        given:
        def invoice1 = TestHelpers.invoice(1)
        invoice1.setId(1)
        def invoice2 = TestHelpers.invoice(2)
        invoice2.setId(2)
        helper_post(invoice1)
        helper_post(invoice2)

        expect:
        mockMvc.perform(delete("/invoices/1"))
                .andExpect(status().isNoContent())

        and:
        mockMvc.perform(get("/invoices/1"))
                .andExpect(status().isNotFound())

        and:
        mockMvc.perform(delete("/invoices/1"))
                .andExpect(status().isNotFound())

        and:
        mockMvc.perform(get("/invoices/2"))
                .andExpect(status().isOk())

        and:
        String responseGetAll = mockMvc.perform(get("/invoices"))
                .andReturn()
                .response
                .getContentAsString()

        def invoiceList = jsonService.readJsonAsObject(responseGetAll, Invoice[])
        invoiceList.size() == 1
    }

    def "should return status not found when try to delete invoice with wrong id"() {

        given:
        def invoice1 = TestHelpers.invoice(1)
        invoice1.setId(1)
        def invoice2 = TestHelpers.invoice(2)
        invoice2.setId(2)
        helper_post(invoice1)
        helper_post(invoice2)

        expect:
        mockMvc.perform(delete("/invoices/3"))
                .andExpect(status().isNotFound())

    }

    def 'should modify 1 invoice'() {

        given:
        def invoice1 = TestHelpers.invoice(1)
        invoice1.setId(1)
        helper_post(invoice1)
        def modifiedInvoice1 = invoice1
        modifiedInvoice1.date = LocalDate.of(2022, 4, 8)
        def invoiceAsJson = jsonService.writeObjectAsJson(modifiedInvoice1)

        expect:
        mockMvc.perform(put("/invoices/1").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())

        and:
        String responseAfterModification = mockMvc.perform(get("/invoices/1"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .getContentAsString()

        def invoiceAfterModification = jsonService.readJsonAsObject(responseAfterModification, Invoice)
        invoiceAfterModification.getDate() == LocalDate.of(2022, 4, 8)
    }
}
